package com.bilibili_fake.demo.service;

import com.bilibili_fake.demo.entity.*;
import com.bilibili_fake.demo.repository.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.File;
import java.sql.Array;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class VideoService {
    private Users users;
    private Videos videos;
    @Autowired
    private VideosRepository videosRepository;
    @Autowired
    private CollectionRepository collectionRepository;
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ClassificationRepository classificationRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private WatchRepository watchRepository;
    @Autowired
    private CommentaryRepository commentaryRepository;

    public JSONObject create(String video, String title, String introduction, String thumb, String category, int userid, int duration) throws JSONException {
        JSONObject object = new JSONObject();
        int views=0,collections=0,like=0,grades=0,comments=0;
        SimpleDateFormat format = new SimpleDateFormat();//设置时间格式
        format.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Classification classification = classificationRepository.findInfoByType(category);
        videosRepository.createVideo(title,introduction,userid,format.format(date),thumb,duration,video,classification.getTypeid(),views,collections,comments,like,grades);
        object.put("code", 200);
        object.put("message","上传成功");
        return object;
    }

    //删除视频:查询该视频是否有评论表、点赞表、播放表，如果有则删除
    public JSONObject deleteVideo(Integer videoId) throws JSONException {
        JSONObject object = new JSONObject();
        List<Watch> watch = watchRepository.findInfoById(videoId);
        if (watch!=null) {
            watchRepository.deleteInfoById(videoId);
        }
        List<Collection> collection = collectionRepository.findByVideoId(videoId);
        if (collection!=null) {
            collectionRepository.deleteInfoByVid(videoId);
        }
        List<Likes> likes = likesRepository.findByVideoId(videoId);
        if (likes!=null) {
            likesRepository.deleteInfoByVid(videoId);
        }
        List<Commentary> commentary = commentaryRepository.findInfoByVideoId(videoId);
        if (commentary!=null) {
            commentaryRepository.deleteInfoByVid(videoId);
        }
        videosRepository.deleteVideo(videoId);//删除视频表
        videos = videosRepository.findInfoById(videoId);
        watch = watchRepository.findInfoById(videoId);
        collection = collectionRepository.findByVideoId(videoId);
        likes = likesRepository.findByVideoId(videoId);
        commentary = commentaryRepository.findInfoByVideoId(videoId);
        if (videos != null || watch != null || collection != null || likes != null || commentary != null) {
            object.put("code",400);
            object.put("message", "删除视频失败");
            return object;
        }
        object.put("code",200);
        object.put("message", "删除视频成功");
        return object;
    }

    //已上传视频
    public JSONObject uploaded(Integer page, Integer userId) throws JSONException {
        int total=0;
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        List<Videos> videosList;
        videosList = videosRepository.findInfoByUserId((page-1)*10,userId,10);
        if (videosList == null){
            object.put("code", 400);
            object.put("message", "请求失败");
            return object;
        }
        object.put("code", 200);
        object.put("message", "请求成功");
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        for (Videos video:videosList) {
            total++;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", video.getVideoid());
            jsonObject.put("title",video.getTitle());
            jsonObject.put("duration", format.format(video.getDuration()));
            jsonObject.put("thumbUrl", video.getThumburl());
            jsonObject.put("visitCount", video.getViews());
            jsonObject.put("collectCount",video.getCollections());
            jsonObject.put("likeCount", video.getLikes());
            array.put(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("videos",array);
        jsonObject.put("total",((total-9)/10)+1);
        object.put("data", jsonObject);
        return object;
    }

    //观看历史
    public JSONObject history(Integer page,Integer userId) throws JSONException {
        List<Videos> videosList;
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        int total=0;
        List<Watch> watches = watchRepository.getInfo((page-1)*10,userId,10);
        if (watches == null){
            object.put("code", 400);
            object.put("message", "请求失败");
            return object;
        }
        object.put("code", 200);
        object.put("message", "请求成功");
        for (Watch watch:watches) {
            total++;
            Videos videos = videosRepository.findInfoById(watch.getVideoid());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", videos.getVideoid());
            jsonObject.put("title", videos.getTitle());
            jsonObject.put("thumbUrl", videos.getThumburl());
            array.put(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("videos",array);
        jsonObject.put("total",((total-9)/10)+1);
        object.put("data", jsonObject);
        return object;
    }

    //点赞视频
    public JSONObject likeVideo(Integer videoId, int userid) throws JSONException {
        JSONObject object = new JSONObject();
        videos = videosRepository.findInfoById(videoId);
        if (videos == null) {
            object.put("code", 400);
            object.put("message", "查询视频不存在");
            return object;
        }
        if (likesRepository.findInfo(userid,videoId) == null) {
            likesRepository.insertLikes(userid,videoId);
            object.put("code",200);
            object.put("message","点赞成功");
            return object;
        }
        likesRepository.deleteInfo(userid,videoId);
        object.put("code",200);
        object.put("message","取消点赞成功");
        return object;
    }

    //收藏视频
    public JSONObject collect(Integer videoId, int userid) throws JSONException {
        JSONObject object = new JSONObject();
        videos = videosRepository.findInfoById(videoId);
        if (videos == null) {
            object.put("code", 400);
            object.put("message", "查询视频不存在");
            return object;
        }
        if (collectionRepository.findByUserId(userid,videoId) == null) {
            collectionRepository.insertCollect(userid,videoId);
            object.put("code",200);
            object.put("message","收藏成功");
            return object;
        }
        object.put("code", 400);
        object.put("message", "已收藏，请不要重复收藏");
        return object;
    }

    //取消收藏
    public JSONObject cancelCollect(Integer videoId, int userid) throws JSONException {
        int collects;
        JSONObject object = new JSONObject();
        videos = videosRepository.findInfoById(videoId);
        if (videos == null) {
            object.put("code", 400);
            object.put("message", "查询视频不存在");
            return object;
        }
        if (collectionRepository.findInfoByUserId(userid) != null) {
            collectionRepository.deleteCollect(userid,videoId);
            object.put("code",200);
            object.put("message","操作成功");
            return object;
        }
        object.put("code", 400);
        object.put("message", "查询记录不存在");
        return object;
    }

    //已收藏视频
    public JSONObject collected(Integer page,Integer userId) throws JSONException {
        int total = 0;
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        List<Collection> collections = collectionRepository.findInfo((page-1)*10,userId,10);
        if (collections == null) {
            object.put("code",200);
            object.put("message","你还没有收藏视频，快去收藏吧");
            return object;
        }
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        for (Collection data:collections) {
            total++;
            Videos videos = videosRepository.findInfoById(data.getVideoid());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", videos.getVideoid());
            jsonObject.put("title", videos.getTitle());
            jsonObject.put("thumbUrl", videos.getThumburl());
            jsonObject.put("duration", format.format(videos.getDuration()));
            array.put(jsonObject);
        }
        object.put("code", 200);
        object.put("message", "查询成功");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("videos",array);
        jsonObject.put("total",((total-9)/10)+1);
        object.put("data", jsonObject);
        return object;
    }

    //获取视频分类
    public JSONObject classifications() throws JSONException {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        List<Classification> classification = classificationRepository.findInfo();
        if (classification == null) {
            object.put("code", 400);
            object.put("message", "内容为空");
            return object;
        }
        for (Classification data:classification) {
            array.put(data.getVideotype());
        }
        object.put("code", 200);
        object.put("message", "获取成功");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("categories",array);
        object.put("data",jsonObject);
        return object;
    }

    //视频排序方式
    public JSONObject orders() throws JSONException {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        object.put("code", 200);
        object.put("message","查询成功");
        JSONObject jsonObject = new JSONObject();
        array.put("综合排序");
        array.put("最多点击");
        array.put("最新发布");
        jsonObject.put("orders", array);
        object.put("data", jsonObject);
        return object;
    }

    //搜索视频
    //classification:视频分类方式
    public JSONObject searchVideo(Integer page, String classification, String keyword, String order) throws JSONException {
        int total = 0;
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        List<Videos> videosList = null;
        page--;
        if (classification == null) {//分类方式为空
            if (keyword == null) {
                if (order.equals("综合排序")) {
                    videosList = videosRepository.searchByGrades(page*10,order,10);//排序方式：综合排序
                } else if (order.equals("最多点击")) {
                    videosList = videosRepository.searchByViews(page*10,order,10);//排序方式：最多点击
                } else if (order.equals("最新发布")) {
                    videosList = videosRepository.searchByTime(page*10,10);//排序方式：最新发布
                }
            } else {
                if (order.equals("综合排序")) {
                    videosList = videosRepository.searchAllVideoByGrades(page*10,keyword,order,10);//排序方式：综合排序
                } else if (order.equals("最多点击")) {
                    videosList = videosRepository.searchAllVideoByViews(page*10,keyword,order,10);//排序方式：最多点击
                } else if (order.equals("最新发布")) {
                    videosList = videosRepository.searchAllVideoByTime(page*10,keyword,10);//排序方式：最新发布
                }
            }
        } else {
            Classification classifi = classificationRepository.findInfoByType(classification);
            if (keyword == null) {
                if (order == null) {
                    videosList = videosRepository.searchInfo(page*8,classifi.getTypeid(),8);
                } else if (order.equals("综合排序")) {
                    videosList = videosRepository.searchAllByGrades(page*10,order,10,classifi.getTypeid());//排序方式：综合排序
                } else if (order.equals("最多点击")) {
                    videosList = videosRepository.searchAllByViews(page*10,order,10,classifi.getTypeid());//排序方式：最多点击
                } else if (order.equals("最新发布")) {
                    videosList = videosRepository.searchAllByTime(page*10,10,classifi.getTypeid());//排序方式：最新发布
                }
            } else {
                if (order.equals("综合排序")) {
                    videosList = videosRepository.searchVideoByGrades(page*10,keyword,order,10,classifi.getTypeid());//排序方式：综合排序
                } else if (order.equals("最多点击")) {
                    videosList = videosRepository.searchVideoByViews(page*10,keyword,order,10,classifi.getTypeid());//排序方式：最多点击
                } else if (order.equals("最新发布")) {
                    videosList = videosRepository.searchVideoByTime(page*10,keyword,10,classifi.getTypeid());//排序方式：最新发布
                }
            }
        }
        for (Videos data:videosList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", data.getVideoid());
            jsonObject.put("title", data.getTitle());
            jsonObject.put("duration", data.getDuration());
            jsonObject.put("thumbUrl", data.getThumburl());
            array.put(jsonObject);
        }
        object.put("code",200);
        object.put("message", "查询成功");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("videos",array);
        jsonObject.put("total", ((total-9)/10)+1);
        object.put("data", jsonObject);
        return object;
    }

    //首页轮播图
    public JSONObject carousel() throws JSONException {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        List<Videos> videosList;
        videosList = videosRepository.findCarousel();
        if (videosList == null) {
            object.put("code",400);
            object.put("message","查询失败");
            return object;
        }
        object.put("code",200);
        object.put("message","查询成功");
        for (Videos data:videosList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", data.getVideoid());
            jsonObject.put("title", data.getTitle());
            jsonObject.put("introduction", data.getIntro());
            jsonObject.put("thumbUrl", data.getThumburl());
            array.put(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("videos", array);
        object.put("data", jsonObject);
        return object;
    }

    //视频详情
    public JSONObject detail(Integer videoId,Integer userid) throws JSONException {
        JSONObject object = new JSONObject();
        SimpleDateFormat format = new SimpleDateFormat();//设置时间格式
        format.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        videos = videosRepository.findInfoById(videoId);
        if (videos == null) {
            object.put("code", 400);
            object.put("message", "查询视频不存在");
            return object;
        }
        object.put("code", 200);
        object.put("message", "查询成功");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID", videos.getVideoid().toString());
        jsonObject.put("title",videos.getTitle());
        jsonObject.put("duration", videos.getDuration().toString());
        jsonObject.put("introduction", videos.getIntro());
        jsonObject.put("uploadTime",videos.getUploadtime());
        jsonObject.put("UID", videos.getUserid());
        users = usersRepository.findInfoById(videos.getUserid());
        jsonObject.put("nickname",users.getNickname());
        jsonObject.put("avatarUrl", users.getAvatarurl());
        jsonObject.put("thumbUrl", videos.getThumburl());
        jsonObject.put("collectCount", videos.getCollections());
        jsonObject.put("visitCount", videos.getViews());
        jsonObject.put("likeCount", videos.getLikes());
        jsonObject.put("url",videos.getVideourl());
        if (watchRepository.findInfo(userid,videoId) == null) {
            watchRepository.insertInfo(userid,videoId,format.format(date));
        } else {
            watchRepository.updateInfo(userid,videoId,format.format(date));
        }
//        JSONArray array = new JSONArray(jsonObject);
        object.put("data", jsonObject);
        return object;
    }

    //相关视频推荐
    public JSONObject postComment(Integer videoId) throws JSONException {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        List<Videos> videosList;
        videos = videosRepository.findInfoById(videoId);
        if (videos == null) {
            object.put("code",400);
            object.put("message","视频不存在");
            return object;
        }
        videosList = videosRepository.findPostComment(videos.getTypeid());
        object.put("code",200);
        object.put("message","查询成功");
        for (Videos data:videosList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", data.getVideoid());
            jsonObject.put("title", data.getTitle());
            jsonObject.put("duration", data.getDuration());
            jsonObject.put("thumbUrl", data.getThumburl());
            array.put(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("videos", array);
        object.put("data", jsonObject);
        return object;
    }
}
