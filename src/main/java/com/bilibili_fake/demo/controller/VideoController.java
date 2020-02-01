package com.bilibili_fake.demo.controller;

import com.bilibili_fake.demo.entity.Users;
import com.bilibili_fake.demo.service.VideoService;
import com.bilibili_fake.demo.utils.VideoProcessor;
import it.sauronsoftware.jave.EncoderException;
import org.apache.shiro.SecurityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

@RestController
@RequestMapping("/video")
public class VideoController extends BaseControlller {
    private final VideoService videoService;
    private VideoProcessor videoProcessor;
    Users users;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /*
    上传视频
    data:视频存放地点
    title:视频标题
    introduce:视频简介
    thumb:封面图片
    classification:分类名称
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadVideo(@RequestParam("data") MultipartFile video,
                              @RequestParam("title") String title,
                              @RequestParam("introduction") String introduction,
                              @RequestParam("thumb") MultipartFile thumb,
                              @RequestParam("category") String category) throws JSONException, SQLException, IOException, EncoderException {
        JSONObject object;
        int duration = 0;
        String thumbPath = "/thumbdata";
        String videoPath = "/videodata";
        videoProcessor = new VideoProcessor();
        videoProcessor.saveFile("D:\\IDEA\\staticfile\\"+videoPath,video);
        duration = (int) videoProcessor.getDuration(video);
        videoProcessor.saveFile("D:\\IDEA\\staticfile\\"+thumbPath,thumb);
        users = (Users) SecurityUtils.getSubject().getPrincipal();
        object = videoService.create(videoPath+"/"+video.getOriginalFilename(),title,introduction,thumbPath+"/"+thumb.getOriginalFilename(),category,users.getUserid(),duration);
        return object.toString();
    }


    /*
    删除已上传视频
    videoId:视频编号
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public String deleteVideo(@RequestParam("videoID") Integer videoId) throws JSONException {
        JSONObject object = null;
        object = videoService.deleteVideo(videoId);
        return object.toString();
    }

    /*
    已上传视频
    page:页号
    UID:用户编号
     */
    @RequestMapping("/uploaded")
    public String uploaded(@RequestParam("page") Integer page,
                           @RequestParam(value = "UID", required = false) Integer userid) throws JSONException {
        JSONObject object;
        users = (Users) SecurityUtils.getSubject().getPrincipal();
        if (userid == null) {
            object = videoService.uploaded(page,users.getUserid());
        } else {
            object = videoService.uploaded(page,userid);
        }
        return object.toString();
    }

    /*
    视频观看记录
    page:页号
     */
    @RequestMapping("/history")
    public String history(@RequestParam("page") Integer page) throws JSONException {
        JSONObject object = null;
        users = (Users) SecurityUtils.getSubject().getPrincipal();
        object = videoService.history(page,users.getUserid());
        return object.toString();
    }

    /*
    点赞视频
    videoId:视频编号
     */
    @RequestMapping(value = "/like", method = RequestMethod.PUT)
    public String likeVideo(@RequestBody HashMap<String,Object> map,
                            HttpServletRequest request) throws JSONException {
        HashMap params = (HashMap) map.get("params");
        String videoId = params.get("ID").toString();
        users = (Users) SecurityUtils.getSubject().getPrincipal();
        if (users == null) {
            JSONObject object = new JSONObject();
            object.put("code","400");
            object.put("message","您现在还没有登录");
            return object.toString();
        }
        return videoService.likeVideo(Integer.valueOf(videoId),users.getUserid()).toString();
    }

    /*
    收藏视频
    VideoId:视频编号
     */
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public String collect(@RequestBody HashMap<String,Object> map,
                          HttpServletRequest request) throws JSONException {
        HashMap params = (HashMap) map.get("params");
        String videoId = params.get("ID").toString();
        users = (Users) SecurityUtils.getSubject().getPrincipal();
        if (users == null) {
            JSONObject object = new JSONObject();
            object.put("code","400");
            object.put("message","您现在还没有登录");
            return object.toString();
        }
        return videoService.collect(Integer.valueOf(videoId),users.getUserid()).toString();
    }

    /*
    取消收藏视频
    videoId:视频编号
     */
    @RequestMapping(value = "/cancelCollect", method = RequestMethod.DELETE)
    public String cancelCollect(@RequestParam("videoID") Integer videoId) throws JSONException {
        JSONObject object;
        users = (Users) SecurityUtils.getSubject().getPrincipal();
        object = videoService.cancelCollect(videoId,users.getUserid());
        return object.toString();
    }

    /*
    已收藏视频
    page:页号
     */
    @RequestMapping("/collected")
    public String collected(@RequestParam("page") Integer page) throws JSONException {
        JSONObject object = null;
        users = (Users) SecurityUtils.getSubject().getPrincipal();
        object = videoService.collected(page,users.getUserid());
        return object.toString();
    }

    /*
    视频分类
     */
    @RequestMapping("/categories")
    public String classifications() throws JSONException {
        JSONObject object;
        object = videoService.classifications();
        return object.toString();
    }

    /*
    视频排序方式
     */
    @RequestMapping("/orders")
    public String orders() throws JSONException {
        JSONObject object;
        object = videoService.orders();
        return object.toString();
    }

    /*
    搜索视频
    page:页号
    category:视频分类
    keyword:关键词
    order:排序方式
     */
    @RequestMapping("/search")
    public String searchVideo(@RequestParam("page") Integer page,
                              @RequestParam(value = "category",required = false) String classification,
                              @RequestParam(value = "keyword",required = false) String keyword,
                              @RequestParam(value = "order", required = false) String order) throws JSONException {
        JSONObject object = null;
        object = videoService.searchVideo(page,classification,keyword,order);
        return object.toString();
    }

    /*
    首页轮播图推荐视频
     */
    @RequestMapping("/carousel")
    public String carousel() throws JSONException {
        JSONObject object = null;
        object = videoService.carousel();
        return object.toString();
    }

    /*
    视频详细信息
    videoId:视频编号
     */
    @RequestMapping("/detail")
    public String detail(@RequestParam(value = "ID") String videoId) throws JSONException {
        JSONObject object = new JSONObject();
        users = (Users) SecurityUtils.getSubject().getPrincipal();
        if (users == null) {
            object = videoService.detail(Integer.valueOf(videoId),6);
        } else {
            object = videoService.detail(Integer.valueOf(videoId),users.getUserid());
        }
        return object.toString();
    }

    /*
    相关视频推荐
    videoId:视频编号
     */
    @RequestMapping("/relative")
    public String postComment(@RequestParam(value = "videoID") String videoId) throws JSONException {
        return videoService.postComment(Integer.valueOf(videoId)).toString();
    }

    public void setVideoProcessor(VideoProcessor videoProcessor) {
        this.videoProcessor = videoProcessor;
    }
}
