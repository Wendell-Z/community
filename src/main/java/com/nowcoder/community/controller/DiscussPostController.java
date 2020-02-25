package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(value = "/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @PostMapping(value = "/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = userHolder.getUser();
        if (user == null) {
            //未登录
            return CommunityUtil.getString(403, "你还没有登录！");
        }

        if (null == title || StringUtils.isBlank(title)) {
            return CommunityUtil.getString(1, "标题不能为空！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        // 报错的情况,将来统一处理.
        return CommunityUtil.getString(200, "发布成功！");
    }

    /**
     * 返回帖子的详情：一条帖子应包括帖子的内容，对应的用户；帖子的评论，对应的用户；评论的回复，对应的用户
     *
     * @param discussPostId
     * @param model
     * @param page
     * @return
     */
    @GetMapping(value = "/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //根据帖子的ID获取帖子内容和对应用户
        DiscussPost post = discussPostService.selectDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        System.out.println("用户：" + user.getUsername() + " 帖子：" + post.getTitle() + " 赞数：" + likeCount);
        model.addAttribute("likeCount", likeCount);
        // 点赞状态
        int likeStatus = userHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(ENTITY_TYPE_POST, discussPostId, userHolder.getUser().getId());
        model.addAttribute("likeStatus", likeStatus);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //根据帖子对应的userID查询帖子下的评论
        List<Comment> commentList = commentService.selectCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //每一条评论 及其对应作者
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                // 点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                // 点赞状态
                likeStatus = userHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(ENTITY_TYPE_COMMENT, comment.getId(), userHolder.getUser().getId());
                commentVo.put("likeStatus", likeStatus);

                //每一条评论的回复
                //是否有回复
                List<Comment> replyList = commentService.selectCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        replyVo.put("target", userService.findUserById(reply.getTargetId()));
                        // 点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus = userHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(ENTITY_TYPE_COMMENT, reply.getId(), userHolder.getUser().getId());
                        replyVo.put("likeStatus", likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                // 回复数量
                int replyCount = commentService.findCommentCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
