package com.bilibili_fake.demo.service;

import com.bilibili_fake.demo.entity.Users;
import com.bilibili_fake.demo.entity.Videos;
import com.bilibili_fake.demo.repository.FollowingRepository;
import com.bilibili_fake.demo.repository.LikesRepository;
import com.bilibili_fake.demo.repository.UsersRepository;
import com.bilibili_fake.demo.repository.VideosRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Objects;

@Service
public class UsersService {
    private UsersRepository usersRepository;
    private FollowingRepository followingRepository;
    private VideosRepository videosRepository;
    private LikesRepository likesRepository;

    public UsersService(UsersRepository usersRepository, FollowingRepository followingRepository,
                        VideosRepository videosRepository, LikesRepository likesRepository){
        this.usersRepository = usersRepository;
        this.followingRepository = followingRepository;
        this.videosRepository = videosRepository;
        this.likesRepository = likesRepository;
    }

    /**
    * 修改用户信息
    * @param nickname 用户昵称
    * @param email 邮箱
    * @return 返回修改结果
     */
    public JSONObject updateUserInfo(Users user, String nickname, String email) throws JSONException{
        JSONObject json = new JSONObject();
        user.setNickname(nickname);
        user.setEmail(email);
        usersRepository.updateUserInfo(user);
        json.put("code",200);
        json.put("message","修改成功。");
//        System.out.println(json);
        return json;
    }

    /**
    * 获取用户信息
    * @param uid 用户ID
    * @return 返回用户编号、用户名、头像路径、昵称、邮箱
     */
    public JSONObject getUserInfo(Integer uid) throws JSONException{
        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();//用于存放用户信息
        Users user = usersRepository.getUserById(uid);
        Number UID = user.getUserid();
        jsonObject.put("UID",UID);
        jsonObject.put("username",user.getUsername());
        jsonObject.put("avatarUrl",user.getAvatarurl());
        jsonObject.put("nickname",user.getNickname());
        jsonObject.put("email",user.getEmail());
        json.put("code",200);
        json.put("message","信息获取成功");
        json.put("data",jsonObject);
        return json;
    }

    /**
    * 修改头像
    * @param user 用户信息
    * @param imageData 头像图片数据
    * @return 新头像图片路径
     */
    public JSONObject setAvatar(Users user, MultipartFile imageData) throws JSONException{
        JSONObject json = new JSONObject();
        OutputStream os = null;
        InputStream inputStream = null;
        String filePath = null;
        String avatarUrl = null;
        try {
            inputStream = imageData.getInputStream();
            if(user.getAvatarurl().equalsIgnoreCase("/1575168991169.jpg")){
                avatarUrl = "/" + System.currentTimeMillis() + ".png";
            }else{
                avatarUrl = user.getAvatarurl();
            }
            filePath = "D:\\IDEA\\staticfile\\";
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
            File tempFile = new File(Objects.requireNonNull(filePath));
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() + File.separator + avatarUrl);
            // 开始读取
            while ((len = Objects.requireNonNull(inputStream).read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            json.put("code",400);
            json.put("message","头像图片文件创建失败！");
            e.printStackTrace();
            return json;
        } finally {
            // 完毕，关闭所有链接
            try {
                Objects.requireNonNull(os).close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(user.getUserid());
        usersRepository.updateAvatar(user.getUserid(), avatarUrl);
        json.put("code",200);
        json.put("message","头像修改成功。");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newAvatarUrl", avatarUrl);
        json.put("data",jsonObject);
        return json;
    }

    /**
    * 获取用户统计信息
    * @param uid 用户ID
    * @return 返回作品数量、粉丝数量、作品播放数
     */
    public JSONObject getStatistic(Integer uid) throws JSONException{
        JSONObject json = new JSONObject();
        System.out.println("testtest");
        Integer fans = followingRepository.countFans(uid);
        List<Videos> videosList = videosRepository.getInfoByUID(uid);
        Integer videos = videosList.size();
        Integer viewNumber = 0;
        Integer likes = 0;
        for(Videos i:videosList){
            viewNumber += i.getViews();
            likes += i.getLikes();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("videoCount",videos);
        jsonObject.put("fansCount",fans);
        jsonObject.put("likeCount",likes);
        jsonObject.put("visitCount",viewNumber);
        json.put("code",200);
        json.put("message","数据获取成功。");
        json.put("data",jsonObject);
        System.out.println(json);
        return json;
    }
}
