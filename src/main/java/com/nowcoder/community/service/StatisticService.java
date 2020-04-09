package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class StatisticService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyMMdd");

    public void statisticUV(String ip) {
        String date = df.format(new Date());
        String UVKey = RedisKeyUtil.getUVKey(date);
        redisTemplate.opsForHyperLogLog().add(UVKey, ip);
    }

    public long getUVCount(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)) {
            String UVKey = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(UVKey);
            calendar.add(Calendar.DATE, 1);
        }
        String UVKeyUnion = RedisKeyUtil.getUVKeyUnion(df.format(startDate), df.format(endDate));
        redisTemplate.opsForHyperLogLog().union(UVKeyUnion, keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(UVKeyUnion);

    }

    public void statisticDAU(int userId) {
        String DAUKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(DAUKey, userId, true);
    }

    public long getDAUCount(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (!calendar.getTime().after(endDate)) {
            String DAUKey = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(DAUKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        return (long) redisTemplate.execute((RedisCallback) connection -> {
            String DAUKeyUnion = RedisKeyUtil.getDauKeyUnion(df.format(startDate), df.format(endDate));
            connection.bitOp(RedisStringCommands.BitOperation.OR, DAUKeyUnion.getBytes(), keyList.toArray(new byte[0][0]));
            return connection.bitCount(DAUKeyUnion.getBytes());
        });
    }


}
