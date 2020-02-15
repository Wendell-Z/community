package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.holder.UserHolder;
import com.nowcoder.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping(value = "/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserHolder userHolder;

    /**
     * 添加评论 因需要更改帖子中评论数量字段 所以需要传过来帖子ID 另外评论也需要对应帖子ID（前端已处理？）
     * 帖子的类型 对应的userID 前端已一并提交 封装到comment中了
     *
     * @param discussPostId
     * @param comment
     * @return
     */
    @PostMapping(value = "/add/{discussPostId}")
    public String addComment(@PathVariable(value = "discussPostId") int discussPostId, Comment comment) {
        //判空就要异步 加了@ResponseBody 又不能渲染模板
        //解决1 ：前端处理 页面刷新
        //解决2 写一个提示框，有返回的提示数据才显示 没有就不显示
//        if (null == comment.getContent() || StringUtils.isBlank(comment.getContent())){
//
//        }
        //当前用户发的评论
        comment.setUserId(userHolder.getUser().getId());
        //1为被删除
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        //重定向到当前帖子的详情
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
