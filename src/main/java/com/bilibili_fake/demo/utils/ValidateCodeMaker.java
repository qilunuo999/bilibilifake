package com.bilibili_fake.demo.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ValidateCodeMaker {
    private String randString = "0123456789abcdefghijklmnopqrstuvwxyz";//随机产生数字与字母组合的字符串
    private int width = 120;// 图片宽
    private int height = 30;// 图片高
    private float yawpRate = 0.05f;// 噪声率
    private int area = (int) (yawpRate * width * height);//噪点数量
    private Random random = new Random();

    /**
     * 生成随机图片
     */
    public Map<String, Object> getCode(){

        Map<String,Object> returnMap = new HashMap<>();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        graphics.fillRect(0, 0, width, height);//图片大小
        graphics.setFont(new Font("Times New Roman", Font.PLAIN, 18));//字体大小，这是初始设置，修改在下方的getFont方法进行
        graphics.setColor(getRandColor());//字体颜色

        // 干扰线数量
        int lineSize = 40;
        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drawLine(graphics);
        }
        // 绘制随机字符
        String randomString = "";
        // 随机产生字符数量
        int stringNum = 4;
        for (int i = 1; i <= stringNum; i++) {
            randomString = drawString(graphics, randomString, i);
        }
        //添加噪点
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = random.nextInt(256);
            image.setRGB(x,y,rgb);
        }

        //保存图片并返回
        returnMap.put("image",image);
        returnMap.put("randomString",randomString);
        return returnMap;
    }

    /**
     * 绘制字符串
     */
    private String drawString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = getRandomString(random.nextInt(randString
                .length()));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    /**
     * 绘制干扰线
     */
    private void drawLine(Graphics g) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 获取随机的字符
     */
    private String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }

    /**
     * 获得字体
     */
    private Font getFont() {
        return new Font("Fixedsys", Font.BOLD, 24);
    }

    /**
     * 获得颜色
     */
    private Color getRandColor( ) {
        int fc = 110;
        int bc = 133;
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }
}
