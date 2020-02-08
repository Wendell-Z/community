package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping(value = "/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 用户名 邮箱 密码自动绑定
     *
     * @param model
     * @param user
     * @return
     */
    @PostMapping(value = "/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @GetMapping(value = "/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable(value = "userId") int userId, @PathVariable(value = "code") String code) {
        int result = userService.activation(userId, code);
        if (result == 0) {
            model.addAttribute("msg", "激活成功，账号已可以正常使用！");
            model.addAttribute("target", "/login");
        } else if (result == 1) {
            model.addAttribute("msg", "重复操作，账号已被激活！");
            model.addAttribute("target", "/index");
        } else if (result == 2) {
            model.addAttribute("msg", "激活失败，激活码错误！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }
}
