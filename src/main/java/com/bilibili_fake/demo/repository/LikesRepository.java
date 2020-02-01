package com.bilibili_fake.demo.repository;

import com.bilibili_fake.demo.entity.Likes;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface LikesRepository {

    @Select("select * from likes where userid=#{userid} and videoid=#{videoid}")
    Likes findInfo(int userid,int videoid);

    @Insert("insert into likes(userid,videoid) values(#{userid},#{videoId})")
    void insertLikes(int userid, Integer videoId);

    @Select("select * from likes where videoId=#{videoId}")
    List<Likes> findByVideoId(Integer videoId);

    @Delete("delete from likes where videoId=#{videoId}")// order by videoId limit 100
    void deleteInfoByVid(Integer videoId);

    @Delete("delete from likes where videoid=#{userid} and videoid=#{videoId}")
    void deleteInfo(int userid, Integer videoId);
}
