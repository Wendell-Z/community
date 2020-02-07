package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.mapper.DiscussPostMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    private final static Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Test
    public void testDiscussPostMapper() {
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
        rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);

        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post.toString());
        }

        list = discussPostMapper.selectDiscussPosts(149, 0, 10);

        for (DiscussPost post : list) {
            System.out.println(post.toString());
        }
    }

    @Test
    public void log() {
        logger.debug("debug");
        logger.error("error");
        logger.info("info");
        logger.warn("warn");
        logger.trace("trace");
    }
}
