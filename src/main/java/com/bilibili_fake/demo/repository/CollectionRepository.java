package com.bilibili_fake.demo.repository;

import com.bilibili_fake.demo.entity.Collection;
import com.bilibili_fake.demo.entity.Watch;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CollectionRepository {

    @Insert("insert into collection(userid,videoid) values(#{userid},#{videoId})")
    void insertCollect(int userid, Integer videoId);

    @Select("select * from collection where userid=#{userid} and videoid=#{videoid}")
    Collection findByUserId(int userid,Integer videoid);

    @Delete("delete from collection where userid=#{userid} and videoid=#{videoId}")
    void deleteCollect(int userid, Integer videoId);

    @Select("select * from collection where videoId=#{videoId}")
    List<Collection> findByVideoId(Integer videoId);

    @Delete("delete from collection where videoId=#{videoId} order by videoId limit 100")
    void deleteInfoByVid(Integer videoId);

    @Select("select * from collection where userid=#{userid}")
    List<Collection> findInfoByUserId(Integer userid);

    @Select("select * from collection where userid=#{userId} limit #{page},#{size}")
    List<Collection> findInfo(Integer page, Integer userId, Integer size);
}