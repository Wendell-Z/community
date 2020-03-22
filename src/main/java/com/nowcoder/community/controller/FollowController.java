package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.annontation.LoginRequired;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    @LoginRequired
    @PostMapping(value = "/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        followService.follow(userHolder.getUser().getId(), entityType, entityId);
        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(userHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getString(200, "关注成功", null);
    }

    @LoginRequired
    @PostMapping(value = "/unfollow")
    @ResponseBody
    public String unFollow(int entityType, int entityId) {
        User user = userHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getString(200, "已取消关注!");
    }

    @GetMapping(value = "/followees/{userId}")
    public String getFollowees(@PathVariable(value = "userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在！");
        }
        model.addAttribute("user", user);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        page.setPath("/followees/" + userId);
        page.setLimit(5);

        List<Map<String, Object>> list = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (list != null) {
            for (Map<String, Object> map : list) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }

        }
        model.addAttribute("users", list);
        return "/site/followee";
    }

    @GetMapping(value = "/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if (userHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(userHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
