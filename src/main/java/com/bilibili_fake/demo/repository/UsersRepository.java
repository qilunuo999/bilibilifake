package com.bilibili_fake.demo.repository;

import com.bilibili_fake.demo.entity.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UsersRepository {

    @Select("select * from users where userid=#{userid}")
    Users findInfoById(int userid);
    @Insert("insert into users(username,nickname,email,password,avatarurl) values (#{username},#{nickname},#{email},#{password},#{avatarurl})")
    void insertUserInfo(Users user);

    @Update("update users set password=#{newPassword} where username=#{username}")
    void updatePassword(String username, String newPassword);

    @Update("update users set nickname=#{nickname},email=#{email} where userid=#{userid}")
    void updateUserInfo(Users user);

    @Update("update users set avatarurl=#{imageUrl} where userid=#{uid}")
    void updateAvatar(int uid, String imageUrl);

    @Select("select * from users where username=#{username}")
    Users getUserByUsername(String username);

    @Select("select * from users where userid=#{userid}")
    Users getUserById(int uid);
}
