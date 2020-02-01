package com.bilibili_fake.demo.utils;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.SQLException;
import java.util.UUID;

public class VideoProcessor {
    public long getDuration(MultipartFile video) throws IOException, EncoderException {
        Encoder encoder = new Encoder();
        String fileName = video.getContentType();
        String pref  = fileName.indexOf("/") != -1 ? fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length()) : null;
        String prefix = "." + pref;
        final File excelFile = File.createTempFile(UUID.randomUUID().toString().replace("-",""),prefix);
        video.transferTo(excelFile);
        MultimediaInfo m = encoder.getInfo(excelFile);
        long ms = m.getDuration();
        return ms;
    }

    public void saveFile(String path, MultipartFile file) throws IOException {
        OutputStream os = null;
        InputStream inputStream = null;
        String fileName = null;
        try {
            inputStream = file.getInputStream();
            fileName = file.getOriginalFilename();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件
            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //获取流文件
    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
