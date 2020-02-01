package com.bilibili_fake.demo.entity;

import lombok.Data;

@Data
public class Videos {
  private Integer videoid;
  private String title;
  private String intro;
  private Integer userid;
  private java.sql.Timestamp uploadtime;
  private String thumburl;
  private Integer duration;
  private String videourl;
  private Integer typeid;
  private Integer views;
  private Integer collections;
  private Integer comments;
  private Integer likes;
  private Integer grades;
}
