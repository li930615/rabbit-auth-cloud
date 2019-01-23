package com.rabbit.auth.server.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.rabbit.auth.core.exception.RabbitAuthException;
import com.rabbit.auth.server.config.AuthConfig;
import com.rabbit.auth.server.entity.sso.SsoToken;
import com.rabbit.auth.server.handler.SsoHandler;
import com.rabbit.common.entity.CurrentUser;
import com.rabbit.common.util.DynamicObject;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    @ApiOperation(value="获取用户信息", httpMethod="GET")
//    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(name="Authorization", value="token", required=true, dataType="String", paramType="header")})
//    @RequestMapping({"/sso/user"})
//    public ResponseEntity<Object> user(Model model, HttpServletRequest request, HttpServletResponse response)
//    {
//        logger.info("/sso/user...");
//        Map map = new HashMap();
//
//        SsoToken ssoToken = SsoHandler.getSsoToken(request);
//        if (ssoToken == null) {
//            map.put("message", "未提供认证信息或认证信息错误");
//            return new ResponseEntity(map, HttpStatus.UNAUTHORIZED);
//        }
//
//        if (!SsoHandler.verifySsoToken(ssoToken)) {
//            map.put("message", "认证信息过期");
//            return new ResponseEntity(map, HttpStatus.UNAUTHORIZED);
//        }
//
//        SysUserVo userResult = this.sysUserServer.getSysUserVoById(ssoToken.getUser().getId());
//        if (userResult == null) {
//            map.put("message", "用户信息获取失败");
//            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
//        }
//        List<SysUserVo.App> appList = userResult.getAppList();
//        StringBuffer sb = new StringBuffer();
//        for (SysUserVo.App app : appList) {
//            String id = app.getId();
//            sb.append(id).append(",");
//        }
//        List<SysApp> sysAppList = this.sysAppServer.findAppByIds(String.valueOf(sb));
//        if (sysAppList != null && sysAppList.size() > 0) {
//            ArrayList appListNew = new ArrayList<Object>();
//            for (SysUserVo.App app : appList) {
//                for (SysApp sysApp : sysAppList) {
//                    if (!app.getId().equals(sysApp.getId())) continue;
//                    appListNew.add(DynamicObject.createBuilder(SysUserVo.App.class).setFieldValueInObject((Object)app).addFieldValue("iconFile", String.class, (Object)sysApp.getIconFile()).build());
//                }
//            }
//            userResult.setAppList(appListNew);
//        }
//        return new ResponseEntity((Object)userResult, HttpStatus.OK);
//    }

    @GetMapping({"/sso/login"})
    @ApiOperation("单点登录界面")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(name="redirect_url", value="重定项地址", required=true), @io.swagger.annotations.ApiImplicitParam(name="client_id", value="客户端ID", required=true)})
    public String login(Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes)
    {
        logger.info("/sso/login...Get");

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

    @PostMapping({"/sso/login"})
    public String doLogin(Model model, String loginName, String password, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes)
    {
        logger.info("/sso/login...Post");

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
        logger.info("loginName====" + loginName);

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

        SysUserVo data = (SysUserVo)new Gson().fromJson(JSON.toJSONString(result.getData()), SysUserVo.class);

        CurrentUser currentUser = new CurrentUser();
        currentUser.setId(data.getId());
        currentUser.setLoginName(data.getLoginName());

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

    @RequestMapping({"/sso/token"})
    @ApiOperation(value="授权码换取token", httpMethod="POST")
    @ApiImplicitParams({@io.swagger.annotations.ApiImplicitParam(name="code", value="授权码", required=true), @io.swagger.annotations.ApiImplicitParam(name="client_id", value="客户端ID", required=true), @io.swagger.annotations.ApiImplicitParam(name="client_secret", value="客户端密钥", required=true)})
    public ResponseEntity<Map<String, Object>> ssoToken(String code, String client_id, String client_secret, String logoutPath, HttpServletRequest request, HttpServletResponse response)
    {
        Map map = new HashMap();
        logger.info("/sso/token");

        if (StringUtils.isBlank(client_id)) {
            map.put("error", "Bad Request");
            map.put("message", "client_id为空");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }
        String token = SsoHandler.getClientToken(client_id, code);
        logger.info("sso client [{}] auth token [{}] by code ", new Object[] { client_id, token, code });
        boolean flag = false;
        SysApp sysApp = this.sysAppServer.getByCode(client_id);
        if (sysApp != null) {
            String secret = sysApp.getSecret();
            if ((StringUtils.isNotBlank(secret)) && (secret.equals(client_secret))) {
                flag = true;
            }
        }

        if ((StringUtils.isNotBlank(token)) && (flag)) {
            map.put("access_token", token);
            map.put("code", code);
            return new ResponseEntity(map, HttpStatus.OK);
        }
        map.put("error", "Bad Request");
        map.put("message", "token获取失败");
        return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
    }

    public String redirectClient(String clientId, String redirectUrl, String serverCode, RedirectAttributes redirectAttributes)
    {
        logger.info("重定向客户端页面");

        String code = SsoHandler.getClientCode(clientId, serverCode);
        redirectAttributes.addAttribute("code", code);

        return "redirect:" + redirectUrl;
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
}
