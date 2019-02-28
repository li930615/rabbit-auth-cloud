package com.rabbit.auth.server.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.rabbit.auth.core.exception.RabbitAuthException;
import com.rabbit.auth.server.config.AuthConfig;
import com.rabbit.auth.server.entity.sso.SsoToken;
import com.rabbit.auth.server.handler.SsoHandler;
import com.rabbit.common.entity.CurrentUser;
import com.rabbit.feign.ucenter.model.R;
import com.rabbit.feign.ucenter.model.entity.SysApp;
import com.rabbit.feign.ucenter.model.vo.SysUserVo;
import com.rabbit.feign.ucenter.server.SysAppServer;
import com.rabbit.feign.ucenter.server.SysUserServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @ClassName SsoController
 * @Description 单点登录接口
 * @Author LZQ
 * @Date 2019/1/20 14:32
 **/
@Api(value = "SSO单点登录控制器", tags = {"单点登录接口"})
@Controller
public class SsoController {

    private static Logger logger = LoggerFactory.getLogger(SsoController.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    //注入权限配置
    private AuthConfig authConfig;
    @Autowired
    private SysUserServer sysUserServer;
    @Autowired
    private SysAppServer sysAppServer;

    @RequestMapping(value = "/sso")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
        logger.info("/sso...");
        //先去获取token
        SsoToken ssoToken = SsoHandler.getSsoToken(request);
        //如果token为null或者登录验证失败了，那么就重定向到登录界面
        if ((ssoToken == null) || (!SsoHandler.loginCheck(request, response, ssoToken.getCode()))) {
            return "redirect:/sso/login";
        }
        String loginName = ssoToken.getUser().getLoginName();
        model.addAttribute("loginName", loginName);
        //跳转到主面板
        return "index";
    }

    @GetMapping({"/sso/login"})
    @ApiOperation("单点登录界面")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(name="redirect_url", value="重定项地址", required=true), @io.swagger.annotations.ApiImplicitParam(name="client_id", value="客户端ID", required=true)})
    public String login(Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes)
    {
        logger.info("/sso/login...Get");
        //从请求中获取token以及重定向地址
        SsoToken ssoToken = SsoHandler.getSsoToken(request);
        String redirectUrl = request.getParameter("redirect_url");
        String clientId = request.getParameter("client_id");
        String errorMessage = request.getParameter("errorMsg");

        if ((ssoToken != null) && (SsoHandler.loginCheck(request, response, ssoToken.getCode())))
        {
            if (StringUtils.isNotBlank(redirectUrl)) {
                return redirectClient(clientId, redirectUrl, ssoToken.getCode(), redirectAttributes);
            }
            return "redirect:/sso";
        }

        model.addAttribute("area", this.authConfig.area);
        model.addAttribute("company", this.authConfig.company);
        model.addAttribute("copyright", this.authConfig.copyright);
        model.addAttribute("errorMsg", errorMessage);
        model.addAttribute("server", request.getParameter("server"));
        model.addAttribute("redirect_url", request.getParameter("redirect_url"));
        model.addAttribute("client_id", request.getParameter("client_id"));
        return "login";
    }

    /**
     * 功能描述：用户使用用户名、密码提交登录申请
     * @author lizhiqiang
     * @date  2019/2/15
     * @param   loginName
     * @param   password
     * @return
     **/
    @PostMapping({"/sso/login"})
    public String doLogin(Model model, String loginName, String password, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes)
    {
        logger.info("/sso/login...Post");
        //从请求中获取token
        SsoToken ssoToken = SsoHandler.getSsoToken(request);
        if (null != ssoToken)
        {
            SsoHandler.logout(request, response);
        }
        String server = request.getParameter("server");
        String redirectUrl = request.getParameter("redirect_url");
        String clientId = request.getParameter("client_id");
        boolean flag = true;
        String errorMsg = null;
        if (StringUtils.isNotBlank(redirectUrl)) {
            SysApp sysApp = null;
            if (StringUtils.isNotBlank(clientId)) {
                sysApp = this.sysAppServer.getByCode(clientId);
                if (sysApp == null) {
                    errorMsg = "应用未授权";
                    flag = false;
                }
            } else {
                errorMsg = "应用未授权";
                flag = false;
            }
        }
        logger.info("loginName:" + loginName);

        R result = null;
        try
        {
            if (StringUtils.isBlank(loginName)) {
                throw new RabbitAuthException("请输入用户名");
            }
            if (StringUtils.isBlank(password)) {
                throw new RabbitAuthException("请输入密码");
            }
            result = this.sysUserServer.getSysUserVoByLogin(loginName, password);
            if (null == result) {
                throw new RabbitAuthException("登陆失败,请联系管理员！");
            }
            if (result.getCode() != 0)
                throw new RabbitAuthException(result.getMsg());
        }
        catch (RabbitAuthException e) {
            errorMsg = e.getMessage();
        }

        if ((StringUtils.isNotBlank(errorMsg)) || (!flag)) {
            redirectAttributes.addAttribute("errorMsg", errorMsg);
            redirectAttributes.addAttribute("redirect_url", redirectUrl);
            redirectAttributes.addAttribute("client_id", clientId);
            redirectAttributes.addAttribute("server", server);
            return "redirect:/sso/login";
        }

        /*登录成功后返回的R（消息相应体）并转换成json格式的数据*/
        SysUserVo data = new Gson().fromJson(JSON.toJSONString(result.getData()), SysUserVo.class);
        /*实例化一个当前用户对象*/
        CurrentUser currentUser = new CurrentUser();
        /*放入登录用户的ID和账号*/
        currentUser.setId(data.getId());
        currentUser.setLoginName(data.getLoginName());
        /*生成登录服务码并缓存*/
        String serverCode = UUID.randomUUID().toString();
        SsoHandler.saveServerCode(response, serverCode, currentUser);

        if (StringUtils.isNotBlank(server)) {
            return "redirect:" + server;
        }

        if (StringUtils.isNotBlank(redirectUrl)) {
            return redirectClient(clientId, redirectUrl, serverCode, redirectAttributes);
        }
        model.addAttribute("loginName", currentUser.getLoginName());
        return "redirect:/sso";
    }

    @RequestMapping({"/sso/logout"})
    public String logout(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes)
    {
        logger.info("/sso/logout...");

        if (SsoHandler.logout(request, response))
            logger.info("sso server Logout success");
        else {
            logger.info("sso server Logout fail");
        }

        String redictUrl = request.getParameter("redirect_url");

        if (StringUtils.isNotBlank(redictUrl)) {
            return "redirect:" + redictUrl;
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    @RequestMapping({"/sso/verifyCode"})
    @ResponseBody
    public boolean verifyCode(String code, String ssoType)
    {
        logger.info("/verifyCode...{}", code);
        if ("1".equals(ssoType)) {
            return SsoHandler.verifySsoTokenServer(code);
        }
        return SsoHandler.verifySsoTokenClient(code);
    }

    @RequestMapping({"/sso/verify"})
    @ResponseBody
    @ApiOperation(value="验证token", httpMethod="POST")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(name="Authorization", value="token令牌", required=true)})
    public R<String> verify(HttpServletRequest request, String Authorization)
    {
        logger.info("/sso/verify...");
        SsoToken ssoToken = SsoHandler.getSsoToken(request);
        if (SsoHandler.verifySsoToken(ssoToken)) {
            return new R("success");
        }
        return new R(new Exception());
    }

    public String redirectClient(String clientId, String redirectUrl, String serverCode, RedirectAttributes redirectAttributes)
    {
        logger.info("重定向客户端页面");

        String code = SsoHandler.getClientCode(clientId, serverCode);
        redirectAttributes.addAttribute("code", code);

        return "redirect:" + redirectUrl;
    }
}
