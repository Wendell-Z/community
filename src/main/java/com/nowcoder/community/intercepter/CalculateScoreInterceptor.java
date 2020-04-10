package com.nowcoder.community.intercepter;

import com.nowcoder.community.annontation.CalculateScore;
import com.nowcoder.community.annontation.LoginRequired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;

@Component
public class CalculateScoreInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            CalculateScore calculateScore = method.getAnnotation(CalculateScore.class);
            if (calculateScore != null) {
                List<String> headers = (List<String>) response.getHeaderNames();
                for (String header : headers) {
                    System.out.println(header + ": " + response.getHeader(header));
                }
//                Enumeration<String> headers =  request.getAttributeNames();
//                while (headers.hasMoreElements()){
//                    String  s = headers.nextElement();
//                    System.out.println(s + "： "+ request.getAttribute(s));
//                }
            }
        }

    }
}
