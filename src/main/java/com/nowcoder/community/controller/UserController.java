package com.nowcoder.community.controller;

import com.nowcoder.community.annontation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private UserHolder userHolder;

    @GetMapping(value = "/forget")
    public String forget() {
        return "/site/forget";
    }

    @LoginRequired
    @GetMapping(value = "/setting")
    public String setting() {
        return "/site/setting";
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

    /**
     * 这不是个好办法！
     */
    @LoginRequired
    @GetMapping(value = "/upload")
    public void upload() {
    }

    @LoginRequired
    @PostMapping(value = "/upload")
    public String upload(MultipartFile headerImage, Model model) {
        //文件判空
        if (headerImage == null) {
            model.addAttribute("error", "请先选择头像图片！");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }
        // 生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + filename);
        //上传文件
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = userHolder.getUser();
        user.setHeaderUrl(domain + contextPath + "/user/header/" + filename);
        userService.updateHeadImgUrl(user.getId(), user.getHeaderUrl());
        return "redirect:/index";
    }


    @GetMapping(value = "/header/{filename}")
    public void header(@PathVariable(value = "filename") String filename, HttpServletResponse response) {
        //获取文件本地路径
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("img/" + suffix);
        //将文件写入response流中
        try (
                FileInputStream fis = new FileInputStream(filename);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

}
