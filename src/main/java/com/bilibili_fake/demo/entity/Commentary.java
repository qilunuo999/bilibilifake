package com.bilibili_fake.demo.entity;

import lombok.Data;

@Data
public class Commentary {
  private Integer commentid;
  private Integer userid;
  private Integer videoid;
  private String comment;
  private java.sql.Timestamp commenttime;
}
