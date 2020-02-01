package com.bilibili_fake.demo.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageFileWritter {
    private String returnPath = "/code/";//返回图片相对路径
    private String savePath = System.getProperty("user.dir")+"/target/classes/static";//图片保存路径
    public String getSavePath(BufferedImage image)throws IOException {
        String dir = savePath+returnPath;
        File filePath = new File(dir);
        if(!filePath.exists() && !filePath.isDirectory()){
            try{
                filePath.mkdir();
            }catch (Exception e){
                System.out.println(e);
            }

        }
        Random random = new Random();
        for (int i = 1; i <= 15; i++) {
            returnPath += getRandomString(random.nextInt(19));
        }
        returnPath += ".jpg";
        savePath += returnPath;
        System.out.println(savePath);

        ImageIO.write(image, "JPEG", new File(savePath));//生成.jpeg文件
        return returnPath;
    }
    /**
     * 获取随机的字符
     */
    private String getRandomString(int num) {
        //随机产生数字与字母组合的字符串
        String randString = "0123456789abcdefghijklmnopqrstuvwxyz";
        return String.valueOf(randString.charAt(num));
    }
}
