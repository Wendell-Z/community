package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.DiscussPostMapper;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;


    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int selectDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException(("参数不能为空！"));
        }
        //转义HTML标签
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //过滤
        discussPost.setTitle(sensitiveFilter.sensitiveWordFilter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.sensitiveWordFilter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost selectDiscussPostById(int discussPostId) {
        return discussPostMapper.selectDiscussPostById(discussPostId);
    }

    public int updatePostCommentCount(int postId, int commentCount) {
        return discussPostMapper.updateCommentCount(postId, commentCount);
    }

    public int updatePostType(int id, int type) {
        return discussPostMapper.updateDiscussType(id, type);
    }

    public int updatePostStatus(int id, int status) {
        return discussPostMapper.updateDiscussType(id, status);
    }

    public int updatePostScore(int id, double score) {
        return discussPostMapper.updatePostScore(id, score);
    }


}
