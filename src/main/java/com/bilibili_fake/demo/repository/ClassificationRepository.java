package com.bilibili_fake.demo.repository;

import com.bilibili_fake.demo.entity.Classification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ClassificationRepository {

    @Select("select * from classification")
    List<Classification> findInfo();

    @Select("select * from classification where videotype=#{category}")
    Classification findInfoByType(String category);
}
