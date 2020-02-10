package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/forget")
    public String forget() {
        return "/site/forget";
    }

    @PostMapping(value = "/forget")
    public String changePassword(Model model, String email, String verifycode, String password, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)) {
            model.addAttribute("emaileMsg", "邮箱不能为空！");
            return "/site/forget";
        }
        if (StringUtils.isBlank(verifycode)) {
            model.addAttribute("codeMsg", "请输入验证码！");
            return "/site/forget";
        }
        User user = userService.findUserByEmail(email);
        if (null == user) {
            model.addAttribute("emailMsg", "邮箱不存在！");
            return "/site/forget";
        }
        if (null == session.getAttribute(email)) {
            model.addAttribute("codeMsg", "验证码已失效！");
            return "/site/forget";
        }
        String code = (String) session.getAttribute(email);
        if (!verifycode.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码错误！");
            return "/site/forget";
        }
        if (StringUtils.isBlank(password)) {
            model.addAttribute("passwordMsg", "请输入新密码！");
            return "/site/forget";
        }

        int i = userService.updatePassword(email, password, user);
        if (i == 1) {
            //修改密码成功 跳转到登录页面 //设置登录凭证过期没写
            model.addAttribute("msg", "修改密码成功,即将跳转到登录页面！");
            model.addAttribute("target", "/login");
            session.removeAttribute(email);
            return "/site/operate-result";
        } else {
            model.addAttribute("passwordMsg", "服务器异常，修改密码失败！");
            return "/site/forget";
        }
    }

    @GetMapping(value = "/verifycode")
    public String sendCode(String email, Model model, HttpSession session) {
        Map<String, Object> map = userService.sendCode(email);
        //email有问题
        if (map.containsKey("emailMsg")) {
            model.addAttribute("emailMsg", map.get("emailMsg"));
        } else {
            session.setAttribute(email, map.get("code"));
            session.setMaxInactiveInterval(60 * 5);
            model.addAttribute("codeMsg", "验证码已发送，请注意查收！");
        }
        return "/site/forget";
    }
}
