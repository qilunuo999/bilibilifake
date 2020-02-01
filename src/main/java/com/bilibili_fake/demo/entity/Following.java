package com.bilibili_fake.demo.entity;

import lombok.Data;

@Data
public class Following {
  private Integer userid;
  private Integer followed;
  private Integer followingid;
}
