package com.nowcoder.community.Aspect;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.util.RedisKeyUtil;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CalculateScoreAspect {
    @Autowired
    private RedisTemplate redisTemplate;

    @AfterReturning(value = "@annotation(com.nowcoder.community.annontation.CalculateScore))", returning = "responseStr")
    public void afterRetuning(String responseStr) {
        if (responseStr.contains("forward")) {
            Integer postId = Integer.valueOf(responseStr.substring(responseStr.lastIndexOf("/") + 1, responseStr.indexOf("?")));
            putInRedis(postId);

        }
        if (responseStr.contains("200")) {
            JSONObject jsonObject = JSONObject.parseObject(responseStr);
            Integer postId = (Integer) jsonObject.get("postId");
            putInRedis(postId);
        }
    }

    private void putInRedis(int postId) {
        String scoreKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(scoreKey, postId);
    }
}
