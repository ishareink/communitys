package cn.cyc.communitys.dao;

import cn.cyc.communitys.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId")int userId,@Param("offset") int offset,@Param("limit") int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(@Param("id")int id);

    int updateCommentCount(@Param("id")int id,@Param("commentCount") int commentCount);

}
