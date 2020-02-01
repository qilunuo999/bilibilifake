package com.bilibili_fake.demo.repository;

import com.bilibili_fake.demo.entity.Videos;
import org.apache.ibatis.annotations.*;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@Mapper
public interface VideosRepository {

    @Delete("delete from videos where videoid=#{videoId}")
    void deleteVideo(Integer videoId);

    @Select("select * from videos where videoid=#{videoId}")
    Videos findInfoById(Integer videoId);

    @Select("select * from videos where videoid=#{videoId} limit #{page},#{size}")
    List<Videos> findUploaded(Integer page, Integer userId, Integer size);

    @Select("select * from videos where userid=#{userId} limit #{page},#{size}")
    List<Videos> findInfoByUserId(Integer page, Integer userId, Integer size);//

    //修改点赞数点赞
    @Update("update videos set likes=#{likes} where videoid=#{videoId}")
    void updateLikes(int likes, Integer videoId);

    //修改收藏数
    @Update("update videos set collections=#{collections} where videoid=#{videoId}")
    void updateCollects(int collections, Integer videoId);

    //随机查询
    @Select("select * from videos where typeid=#{typeid} order by rand() limit 8")
    List<Videos> findPostComment(int typeid);

    //轮播图
    @Select("select * from videos order by rand() limit 8")
    List<Videos> findCarousel();

    //分类搜索
    @Select("select * from videos where title=#{keyword},videotype=#{classification} order by order desc limit #{page},#{size}")
    List<Videos> searchVideo(Integer page, String keyword, String order, Integer size, String classification);

    //上传新视频
    @Insert("insert into videos(title,intro,userid,uploadtime,thumbUrl,duration,videourl,typeid," +
            "views,collections,comments,likes,grades) values(#{title},#{introduction},#{userid}," +
            "#{format},#{thumb},#{duration},#{video},#{category},#{views},#{collections},#{comments},#{like},#{grades})")
    void createVideo(String title, String introduction, int userid, String format, String thumb,
                     int duration, String video, int category, int views, int collections, int comments, int like, int grades);

    @Select("select * from videos where title like '%${keyword}%' order by grades desc limit #{page},#{size}")
    List<Videos> searchAllVideoByGrades(Integer page, String keyword, String order,Integer size);

    @Select("select * from videos where title like '%${keyword}%' order by views desc limit #{page},#{size}")
    List<Videos> searchAllVideoByViews(Integer page, String keyword, String order, Integer size);

    @Select("select * from videos where title like '%${keyword}%' order by uploadtime desc,videoid desc limit #{page},#{size}")
    List<Videos> searchAllVideoByTime(Integer page, String keyword, Integer size);

    @Select("select * from videos where title like '%${keyword}%' and typeid=#{classification} order by grades desc limit #{page},#{size}")
    List<Videos> searchVideoByGrades(Integer page, String keyword, String order, Integer size, Integer classification);

    @Select("select * from videos where title like '%${keyword}%' and typeid=#{classification} order by views desc limit #{page},#{size}")
    List<Videos> searchVideoByViews(Integer page, String keyword, String order, Integer size, Integer classification);

    @Select("select * from videos where title like '%${keyword}%' and typeid=#{classification} order by uploadtime desc limit #{page},#{size}")
    List<Videos> searchVideoByTime(Integer page, String keyword, Integer size, Integer classification);

    @Select("select * from videos order by grades desc limit #{page},#{size}")
    List<Videos> searchByGrades(Integer page, String order, Integer size);

    @Select("select * from videos order by views desc limit #{page},#{size}")
    List<Videos> searchByViews(Integer page, String order, Integer size);

    @Select("select * from videos order by uploadtime desc,videoid desc limit #{page},#{size}")
    List<Videos> searchByTime(Integer page, Integer size);

    @Select("select * from videos where typeid=#{classification} order by grades desc limit #{page},#{size}")
    List<Videos> searchAllByGrades(Integer page, String order, Integer size, Integer classification);

    @Select("select * from videos where typeid=#{classification} order by views desc limit #{page},#{size}")
    List<Videos> searchAllByViews(Integer page, String order, Integer size, Integer classification);

    @Select("select * from videos where typeid=#{classification} order by uploadtime desc,videoid desc limit #{page},#{size}")
    List<Videos> searchAllByTime(Integer page, Integer size, Integer classification);

    @Select("select * from videos where userid=#{uid}")
    List<Videos> getInfoByUID(Integer uid);

    @Select("select * from videos where typeid=#{classification} limit #{page},#{size}")
    List<Videos> getInfo(Integer page, Integer classification, Integer size);

    @Select("select title from videos where videoid=#{videoid}")
    Videos getTitle(Integer videoid);

    @Select("select * from videos where typeid=#{typeid} limit #{page},#{size}")
    List<Videos> searchInfo(Integer page, Integer typeid, Integer size);
}
