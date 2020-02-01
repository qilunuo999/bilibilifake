package com.bilibili_fake.demo.repository;

import com.bilibili_fake.demo.entity.Watch;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface WatchRepository {

    @Select("select * from watch")
    List<Watch> findInfoById(Integer videoId);

    @Delete("delete from watch where videoId=#{videoId} order by videoId limit 100")
    void deleteInfoById(Integer videoId);

    @Select("select * from watch where userid=#{userid} and videoId=#{videoId}")
    Watch findInfo(Integer userid, Integer videoId);

    @Insert("insert into watch(userid,videoid,playtime) values(#{userid},#{videoId},#{format})")
    void insertInfo(Integer userid, Integer videoId, String format);

    @Update("update watch set playtime=#{format} where userid=#{userid} and videoid=#{videoId}")
    void updateInfo(Integer userid, Integer videoId, String format);

    @Select("select * from watch where userid=#{userId} limit #{page},#{size}")
    List<Watch> getInfo(Integer page, Integer userId, Integer size);
}
