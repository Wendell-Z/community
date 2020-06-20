package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private LikeService likeService;

    // 牛客纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败!", e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String scoreKey = RedisKeyUtil.getPostScoreKey();
        //what is this? 绑定操作
//        redisTemplate.opsForSet().size(scoreKey);
        BoundSetOperations operations = redisTemplate.boundSetOps(scoreKey);
        if (operations.size() == 0) {
            logger.info("任务取消，没有需要刷新的帖子！");
        }

        logger.info("[任务开始] 正在刷新帖子分数：" + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.selectDiscussPostById(postId);
        if (post == null || post.getStatus() == 2) {
            logger.error("该帖子不存在: id = " + postId);
            return;
        }
        int commentCount = post.getCommentCount();
        boolean isWonderful = post.getStatus() == 1;
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        double w = (isWonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;

        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        post.setScore(score);
        discussPostService.updatePostScore(postId, score);
        elasticSearchService.savePost(post);
    }
}
