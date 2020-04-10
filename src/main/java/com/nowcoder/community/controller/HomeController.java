package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * 目前没有userId 写死了为0 后续应整成requestParam
     * 直接访问主页时 orderMode默认为0 显示最新顺序
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping(value = "/index")
    public String getIndexPage(Model model, Page page, @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        //访问来的时候自动把current参数绑定到Page里面了
        //userId写死了
        page.setRows(discussPostService.selectDiscussPostRows(0));
        page.setPath("/index");
        //获取当前页的数据
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        //Map<String,Object> 方便模板遍历取元素？
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost discussPost : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }

    @GetMapping(value = "/error")
    public String getErrorPage() {
        return "/error/500";
    }

    @GetMapping(path = "/denied")
    public String getDeniedPage() {
        return "/error/404";
    }

    @GetMapping(path = "/")
    public String getIndex() {
        return "redirect:/index";
    }
}
