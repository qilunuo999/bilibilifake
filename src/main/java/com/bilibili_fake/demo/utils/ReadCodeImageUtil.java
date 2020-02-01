package com.bilibili_fake.demo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class ReadCodeImageUtil {
    private Map<String, String> validateMap = new HashMap<>();

    public Map<String,String> getValidateCode() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream("/static/codeList.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line="";
        List<String> path = new ArrayList<>();
        List<String> code = new ArrayList<>();
        while((line=br.readLine())!=null){
            String[] temp = line.split(":");
            code.add(temp[0]);
            path.add(temp[1]);
        }
        Random random = new Random();
        int number = random.nextInt(4607);//4608行验证码信息
        validateMap.put("code",code.get(number));
        validateMap.put("filePath",path.get(number));
        return validateMap;
    }
}
