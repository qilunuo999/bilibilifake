package com.bilibili_fake.demo;

import com.bilibili_fake.demo.utils.ImageFileWritter;
import com.bilibili_fake.demo.utils.ValidateCodeMaker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ValidateCodetest {
    private ImageFileWritter imageFileWritter;
    private ValidateCodeMaker validateCodeMaker;

    @BeforeAll
    void init(){
        imageFileWritter = new ImageFileWritter();
        validateCodeMaker = new ValidateCodeMaker();
    }

    @Test
    void produceCodeImageTest(){
        for(int i = 0; i < 5000; i++){
            Map<String,Object> image = validateCodeMaker.getCode();
        }
    }
}
