package com.nowcoder.community.exception;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//表示只监测controller抛出的异常
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //记录日志
        //判断是否是异步异常
        //异步 返回 code msg
        //同步 返回页面
        logger.error("服务器发生异常:" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        //ajax对象
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            String msg = CommunityUtil.getJsonString(500, "服务器发生异常！");
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(msg);
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
