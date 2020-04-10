package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * 获取所有帖子
     * userId=0则获取所有的  userId!=0则获取对应用户的
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    /**
     * @param userId
     * @return
     * @Param注解用于给参数取别名, 如果只有一个参数, 并且在mapper的xml中<if>里使用,则必须加别名
     * userId=0即所有行 ！=0即对应用户的
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    /**
     * 更新帖子类型 置顶 普通（缺省）
     *
     * @param id
     * @return
     */
    int updateDiscussType(int id, int type);

    /**
     * 更新帖子状态 加精 删除 普通（缺省）
     *
     * @param id
     * @return
     */
    int updateDiscussStatus(int id, int status);

    int updatePostScore(int id, double score);
}
