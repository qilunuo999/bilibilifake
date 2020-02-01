package com.bilibili_fake.demo.entity;

import lombok.Data;

@Data
public class Watch {

  private Integer userid;
  private Integer videoid;
  private java.sql.Timestamp playtime;
  private Integer watchid;
}
