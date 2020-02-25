package com.nowcoder.community.controller;

import com.nowcoder.community.annontation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;
    @Autowired
    private UserHolder userHolder;

    /**
     * 点赞后要返回 赞的总数量 已赞的状态
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @LoginRequired
    @PostMapping("/like")
    @ResponseBody
    public String giveLike(int entityType, int entityId, int entityUserId) {
        User user = userHolder.getUser();
        likeService.giveLike(entityType, entityId, user.getId(), entityUserId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(entityType, entityId, user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getString(200, null, map);
    }


}
