//package com.rabbit.auth.server.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @ClassName MyInterceptor
// * @Description  #_自定义拦截器
// *  * 〈拦截器是在面向切面编程中应用的，就是在你的service或者一个方法前调用一个方法，
// *  * 或者在方法后调用一个方法。是基于JAVA的反射机制。拦截器不是在web.xml，比如struts在struts.xml中配置。
// *  * 拦截器，在AOP(Aspect-Oriented Programming)中用于在某个方法或字段被访问之前，进行拦截，然后在之前或
// *  * 之后加入某些操作。拦截是AOP的一种实现策略〉
// * @Author LZQ
// * @Date 2019/1/19 21:09
// **/
//public class MyInterceptor implements HandlerInterceptor {
//
//    Logger logger = LoggerFactory.getLogger(MyInterceptor.class);
//
//    /*预处理拦截器：该方法将在请求处理之前进行调用*/
//    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
//        String portocol = httpServletRequest.isSecure() ? "https://" : "http://";
//        String serverNamePort = portocol + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort();
//        httpServletRequest.setAttribute("basePath", serverNamePort + httpServletRequest.getContextPath());
//        httpServletRequest.setAttribute("zuulBasePath", serverNamePort + "/zuul" + httpServletRequest.getContextPath());
//        return true;
//    }
//
//    /*由preHandle 方法的解释我们知道这个方法包括后面要说到的afterCompletion 方法都只能是在当前所属的Interceptor 的preHandle 方法
//    的返回值为true 时才能被调用。postHandle 方法，顾名思义就是在当前请求进行处理之后，也就是Controller 方法调用之后执行，但是它会
//    在DispatcherServlet 进行视图返回渲染之前被调用，所以我们可以在这个方法中对Controller 处理之后的ModelAndView 对象进行操作*/
//    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
//
//    }
//
//    /*该方法也是需要当前对应的Interceptor 的preHandle 方法的返回值为true 时才会执行。顾名思义，该方法将在整个请求结束之后，也就是在
//    DispatcherServlet 渲染了对应的视图之后执行。这个方法的主要作用是用于进行资源清理工作的*/
//    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
//
//    }
//}
