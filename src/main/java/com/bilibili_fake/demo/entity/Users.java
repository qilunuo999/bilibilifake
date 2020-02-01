package com.bilibili_fake.demo.entity;

import lombok.Data;

@Data
public class Users {
  private Integer userid;
  private String username;
  private String nickname;
  private String email;
  private String password;
  private String avatarurl;
}
