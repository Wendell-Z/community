package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.mapper.CommentMapper;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

import static com.nowcoder.community.util.CommunityConstant.ENTITY_TYPE_POST;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCommentCountByEntity(entityType, entityId);
    }

    //隔离级别 传播机制 回滚检测的异常
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.sensitiveWordFilter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        // 评论是帖子的评论才会执行
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCommentCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updatePostCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }
}
