package com.bilibili_fake.demo.repository;

import com.bilibili_fake.demo.entity.Following;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface FollowingRepository {

    @Insert("insert into following(userid,followed) values (#{userid},#{followed})")
    void insertFollowInfo(Following following);

    @Delete("delete from following where userid=#{userid} and followed=#{followed}")
    void deleteFollowInfo(Following following);

    @Select("select followed from following where userid=#{userId} order by userid asc")
    List<Integer> getFollowed(int userId);

    @Select("select count(*) from following where followed=#{uid}")
    Integer countFans(Integer uid);
}
