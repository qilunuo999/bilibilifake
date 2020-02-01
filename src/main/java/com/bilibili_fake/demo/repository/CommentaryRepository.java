package com.bilibili_fake.demo.repository;

import com.bilibili_fake.demo.entity.Commentary;
import org.apache.ibatis.annotations.Delete;
import com.bilibili_fake.demo.entity.Commentary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CommentaryRepository {

    @Select("select * from commentary where videoId=#{videoId}")
    List<Commentary> findInfoByVideoId(Integer videoId);

    @Delete("delete from commentary where videoId=#{videoId} order by videoId limit 100")
    void deleteInfoByVid(Integer videoId);

    @Insert("insert into commentary (userid,videoid,comment,commenttime) values(#{userid},#{videoid},#{comment},#{commenttime})")
    void saveVideoComment(Commentary commentary);

    @Delete("delete from commentary where commentid=#{commentId}")
    void deleteComment(int commentId);

    //传入的页码需要乘以10
    @Select("select * from (select * from commentary where videoid=#{videoId} order by videoid asc) as a limit #{page},10")
    List<Commentary> getVideoComments(int videoId, int page);

    @Select("select count(*) from (select commentid from commentary where videoid=#{videoid} order by videoid asc) as a")
    int getVideoCommentsPageNumber(int videoId);

    //传入的页码需要乘以10
    @Select("select * from (select * from commentary where userid=#{uid} order by userid asc) as a limit #{page},10")
    List<Commentary> getUserComment(Integer uid, Integer page);

    @Select("select count(*) from (select commentid from commentary where userid=#{uid} order by userid asc) as a")
    int getUserCommentsPageNumber(int uid);
}
