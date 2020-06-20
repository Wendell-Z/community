package com.nowcoder.community.controller;

import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.annontation.CalculateScore;
import com.nowcoder.community.annontation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping(value = "/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserHolder userHolder;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private EventProducer eventProducer;

    /**
     * 添加评论 因需要更改帖子中评论数量字段 所以需要传过来帖子ID 另外评论也需要对应帖子ID（前端已处理？）
     * 帖子的类型 对应的userID 前端已一并提交 封装到comment中了
     *
     * @param discussPostId
     * @param comment
     * @return
     */
    @CalculateScore
    @LoginRequired
    @PostMapping(value = "/add/{discussPostId}")
    public String addComment(@PathVariable(value = "discussPostId") int discussPostId, Comment comment, Page page) {

        DiscussPost post = discussPostService.selectDiscussPostById(discussPostId);
        if (post == null) {
            return "/error/404";
        }
        System.out.println(page.toString());
        //当前用户发的评论

        comment.setUserId(userHolder.getUser().getId());
        //1为被删除
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        if (!(post.getCommentCount() == 0) && post.getCommentCount() % 5 == 0) {
            page.setCurrent(page.getCurrent() + 1);
        }
        //触发评论事件
        Event event = new Event();
        event.setUserId(userHolder.getUser().getId())
                .setTopic(TOPIC_COMMENT)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.selectDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);
        //重定向到当前帖子的详情 会刷新到首页 不得行
        return "forward:/discuss/detail/" + discussPostId + "?current=" + page.getCurrent();
    }
}
