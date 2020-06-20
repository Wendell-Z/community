package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.DiscussPostMapper;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value(("${caffeine.posts.expire-seconds}"))
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    private void init() {
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        //判空
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误！");
                        }
                        //参数key的格式
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误！");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        //可以在这里再加个二级缓存
                        logger.debug("init load post list from DB , offset:" + offset + " ,limit: " + limit + ".");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer integer) throws Exception {
                        logger.debug("init load post list from DB.");
                        return discussPostMapper.selectDiscussPostRows(integer);
                    }
                });
    }


    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode) {
        //先本地缓存 0首页状态  1热门帖子 最新的帖子一直在更新 不建议缓存
        //若过期执行到90行代码时 ，会调用CacheLoader重新从数据库中取缓存
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int selectDiscussPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
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
