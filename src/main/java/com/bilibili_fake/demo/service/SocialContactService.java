package com.bilibili_fake.demo.service;

import com.bilibili_fake.demo.entity.Commentary;
import com.bilibili_fake.demo.entity.Following;
import com.bilibili_fake.demo.entity.Users;
import com.bilibili_fake.demo.entity.Videos;
import com.bilibili_fake.demo.repository.CommentaryRepository;
import com.bilibili_fake.demo.repository.FollowingRepository;
import com.bilibili_fake.demo.repository.UsersRepository;
import com.bilibili_fake.demo.repository.VideosRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
public class SocialContactService {
    private FollowingRepository followingRepository;
    private CommentaryRepository commentaryRepository;
    private UsersRepository usersRepository;
    private VideosRepository videosRepository;

    public SocialContactService(FollowingRepository followingRepository, CommentaryRepository commentaryRepository,
                                UsersRepository usersRepository, VideosRepository videosRepository){
        this.followingRepository = followingRepository;
        this.commentaryRepository = commentaryRepository;
        this.usersRepository = usersRepository;
        this.videosRepository = videosRepository;
    }

    /**
    * 保存视频评论
    * @param videoID 视频ID
    * @param commentText 评论内容
    * @return 返回处理结果
     */
    public JSONObject postComment(Users user, Integer videoID, String commentText) throws JSONException {
        JSONObject json = new JSONObject();
        Commentary commentary = new Commentary();
        commentary.setComment(commentText);
        commentary.setVideoid(videoID);
        commentary.setUserid(user.getUserid());
        commentary.setCommenttime(new Timestamp(System.currentTimeMillis()));
        commentaryRepository.saveVideoComment(commentary);
        json.put("code",200);
        json.put("message","评论发送成功。");
        return json;
    }

    /**
    * 删除评论
    * @param commentId 评论编号
    * @return 返回处理结果
     */
    public JSONObject deleteComment(Integer commentId) throws JSONException{
        JSONObject json = new JSONObject();
        commentaryRepository.deleteComment(commentId);
        json.put("code",200);
        json.put("message","评论删除成功。");
        return json;
    }

    /**
    * 分页获取视频的评论
    * @param videoId 视频编号
    * @param page 页号
    * @return 返回一个视频评论列表，内容包括评论编号、评论用户编号、评论用户昵称、用户头像路径、评论发表日期、评论内容
    * 以及返回评论总页数
     */
    public JSONObject getVideoComments(Integer videoId, Integer page) throws JSONException{
        JSONObject json = new JSONObject();
        List<Commentary> commentaryList = commentaryRepository.getVideoComments(videoId,(page-1) * 10);
        Integer pageNumber = commentaryRepository.getVideoCommentsPageNumber(videoId);
        json.put("code",200);
        json.put("message","视频评论获取成功。");
        JSONArray array = new JSONArray();
        for(Commentary commentary:commentaryList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID",commentary.getCommentid());
            jsonObject.put("UID",commentary.getUserid());
            Users user = usersRepository.getUserById(commentary.getUserid());
            jsonObject.put("nickname",user.getNickname());
            jsonObject.put("avatarUrl",user.getAvatarurl());
            jsonObject.put("postTime",commentary.getCommenttime().toString());
            jsonObject.put("text",commentary.getComment());
            array.put(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comments",array);
        jsonObject.put("total",pageNumber);
        json.put("data",jsonObject);
        return json;
    }

    /**
    * 分页获取用户评论
    * @param uid 用户ID
    * @param page 页号
    * @return 返回一个包含评论编号、评论视频编号、评论视频标题、评论发表日期、评论内容的列表，以及总页数
     */
    public JSONObject getUserComments(Integer uid, Integer page) throws JSONException{
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        Videos video;
        List<Commentary> commentaryList = commentaryRepository.getUserComment(uid,(page-1)*10);
        Integer pageNumber = commentaryRepository.getUserCommentsPageNumber(uid);//总页数
        for (Commentary i:commentaryList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID",i.getCommentid());
            jsonObject.put("videoID",i.getVideoid());
            video = videosRepository.getTitle(i.getVideoid());
            jsonObject.put("videoTitle", video.getTitle());
            jsonObject.put("postTime",i.getCommenttime().toString());
            jsonObject.put("text",i.getComment());
            array.put(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comments",array);
        jsonObject.put("total",((pageNumber-9)/10)+1);
        json.put("code",200);
        json.put("message","用户评论列表获取成功。");
        json.put("data",jsonObject);
        System.out.println(json);
        return json;
    }

    /**
    * 关注用户
    * @param user 当前用户信息
    * @param uid 被关注用户
    * @return 返回处理结果
     */
    public JSONObject followUser(Users user, Integer uid) throws JSONException {
        JSONObject json = new JSONObject();
        Following following = new Following();
        following.setUserid(user.getUserid());
        following.setFollowed(uid);
        followingRepository.insertFollowInfo(following);
        json.put("code",200);
        json.put("message","关注成功。");
        return json;
    }

    /**
    * 取消关注用户
    * @param user 当前用户
    * @param uid 被取消关注的用户
    * @return 返回处理结果
     */
    public JSONObject cancelFollow(Users user, Integer uid) throws JSONException{
        JSONObject json = new JSONObject();
        Following following = new Following();
        following.setUserid(user.getUserid());
        following.setFollowed(uid);
        followingRepository.deleteFollowInfo(following);
        json.put("code",200);
        json.put("message","取消关注成功。");
        return json;
    }

    /**
    * 获取当前用户已关注用户
    * @param user 当前用户信息
    * @return 返回当前用户已关注用户关注列表
     */
    public JSONObject getFollowed(Users user) throws JSONException{
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        List<Integer> followedList = followingRepository.getFollowed(user.getUserid());
        for (Integer i:followedList){
            JSONObject jsonObject = new JSONObject();
            Users followed = usersRepository.getUserById(i);
            jsonObject.put("UID",followed.getUserid());
            jsonObject.put("nickname",followed.getNickname());
            jsonObject.put("avatarUrl",followed.getAvatarurl());
            array.put(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accounts", array);
        json.put("code",200);
        json.put("message","用户关注列表获取成功。");
        json.put("data",jsonObject);
        return json;
    }
}
