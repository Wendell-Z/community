package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    //忽略拦截静态资源
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers(
//                        "/index",
//                        "/register",
//                        "/user/forget",
//                        "/followees/**",
//                        "/followers/**",
//                        "/discuss/detail/**",
//                        "/user/verifycode",
//                        "/user/header/**",
//                        "/user/profile",
//                        "/login",
//                        "/activation/**",
//                        "/kaptcha",
//                        "/search"
//                ).permitAll();
        // 授权
        // 有以下3种权限的人 可以访问指定的路径
        http.authorizeRequests()
                .antMatchers(
                        "/follow",
                        "/unfollow",
                        //"/followees/**",
                        //"/followers/**",
                        "/discuss/add",
                        "/letter/**",
                        "/like",
                        "/user/setting",
                        "/user/upload",
                        // "/user/header/**",
                        // "/user/profile/**",
                        "/notice/**",
                        "/comment/add/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_USER,
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete",
                        "/statistic/**",
                        "/actuator/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll();
        http.exceptionHandling()
                //未登录情况下访问路径
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        String xRquestWith = httpServletRequest.getHeader("x-request-with");
                        if ("XMLHTTPRequest".equals(xRquestWith)) {
                            httpServletResponse.setContentType("plain/text");
                            PrintWriter writer = httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJsonString(403, "你还未登录！"));
                        } else {
                            // 重定向 重新访问登录页面
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
                        }
                    }
                })
                // 登录了访问无权限的路径
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                        String xRquestWith = httpServletRequest.getHeader("x-request-with");
                        if ("XMLHTTPRequest".equals(xRquestWith)) {
                            httpServletResponse.setContentType("plain/text");
                            PrintWriter writer = httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJsonString(403, "你没有访问此功能的权限！"));
                        } else {
                            // 重定向 重新访问无权限提示页面
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/denied");
                        }
                    }
                });

        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行我们自己的退出代码.
        http.logout().logoutUrl("ignoreDefaultLogoutUrl");
//        http.log
    }


}
