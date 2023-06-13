package com.atheima.reggie.filter;
import com.alibaba.fastjson.JSON;
import com.atheima.reggie.common.BaseContext;
import com.atheima.reggie.common.R;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
/**
 * 检查新用户是否完成登录
 * 1.获取本次请求URl
 * 2.判断本次请求是否需要处理
 * 3.弱不需要处理则直接放行
 * 4.判断登录状态 如果登录方形
 * 5.若果未登录则返回登录页面
 */
@Slf4j
@WebFilter(filterName = "loginCheckfilter",urlPatterns = "/*")
@Component
public class LoginCheckFilter  implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        log.info("拦截器doFilter: {}",request.getRequestURI());
        log.info("拦截到请求：{}",request.getRequestURI());
        String[] urls=new String[]{
          "/employee/login",
          "/employee/logout",
          "/employee/index.html",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
        };
// * 1.获取本次请求URl
        String requestURI = request.getRequestURI();
// * 2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        if (check) {
            log.info("本次不需要请求拦截{}"+requestURI);
            filterChain.doFilter(request,response);
// * 3.弱不需要处理则直接放行
            return;
        }

//         * 41.判断登录状态 如果登录方形
      if (request.getSession().getAttribute("employee")!=null) {
          log.info("本次请求用户已经登陆"+requestURI);
          Long employeeid = (Long)request.getSession().getAttribute("employee");
          BaseContext.setCurrentId(employeeid);
          filterChain.doFilter(request,response);
          return;
        }


        //         * 42.判断登录状态 如果登录方形
        if (request.getSession().getAttribute("user")!=null) {
            log.info("本次请求用户已经登陆"+requestURI);
            Long userid = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userid);
            filterChain.doFilter(request,response);
            return;
        }

      log.info("用户未登录");
//       * 5.若果未登录则返回登录页面 通过输出流的方式向客户端返回数据响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
      return;
    }
    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    /**
     * 检查本次请求是否需要放行
     * @param requestUrl
     * @return
     */
    public boolean check(String[] urls, String requestUrl){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if (match==true){
                return true;
            }
        }
        return false;
    }
}
