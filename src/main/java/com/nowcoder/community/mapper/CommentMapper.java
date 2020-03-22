package com.nowcoder.community.mapper;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 根据评论类型查找评论
     *
     * @param entityType 区分帖子评论和帖子的回复
     * @param entytiId   标识该评论的用户
     * @param offset     分页
     * @param limit      分页
     * @return
     */
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 根据评论类型统计评论的数量
     *
     * @param entityType 区分帖子评论和帖子的回复
     * @param entityId   标识该评论的用户
     * @return
     */
    int selectCommentCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
