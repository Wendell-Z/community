package com.nowcoder.community.interceptor;

import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class StatisticInterceptor implements HandlerInterceptor {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private UserHolder userHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        statisticService.statisticUV(ip);
        if (!(userHolder.getUser() == null)) {
            statisticService.statisticDAU(userHolder.getUser().getId());
        }
        return true;
    }
}
