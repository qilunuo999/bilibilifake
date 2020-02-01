package com.bilibili_fake.demo.controller;

import com.bilibili_fake.demo.entity.Users;
import com.bilibili_fake.demo.service.SocialContactService;
import com.bilibili_fake.demo.utils.ValidateUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

@RestController
public class SocialContactController extends BaseControlller {
    private final SocialContactService socialContactService;

    public SocialContactController(SocialContactService socialContactService) {
        this.socialContactService = socialContactService;
    }

    /**
    * 对视频发表评论
    * @param map 包含视频编号和评论内容
    * @return jsonObject 返回处理结果
    */
    @RequiresUser
    @RequestMapping(value = "/comment/post", method = RequestMethod.POST)
    public String postComments(@RequestBody HashMap<String,Object> map) throws JSONException {
        ValidateUtil validateUtil = new ValidateUtil();//初始化校验工具
        HashMap paramList = (HashMap) map.get("params");
        //参数列表校验
        String[] params = new String[2];
        params[0] = paramList.get("videoID").toString();
        params[1] = paramList.get("commentText").toString();
        //空字符串校验
        if(validateUtil.isParamsEmpty(params)){
            return validateUtil.getErrorInfo("评论内容不能为空哟。(￣△￣；)");
        }
        Users user = (Users)SecurityUtils.getSubject().getPrincipal();
        return socialContactService.postComment(user, Integer.valueOf(params[0]),params[1]).toString();
    }

    /**
    * 删除评论
    * @param commentId 评论编号
    * @return jsonObject 返回处理结果
    */
    @RequiresUser
    @RequestMapping(value = "/comment/delete")
    public String deleteComment(@RequestParam("commentID") String commentId) throws JSONException {
        return socialContactService.deleteComment(Integer.valueOf(commentId)).toString();
    }

    /**
    * 分页获取视频评论
    * @param videoID 视频编号
    * @param page 页号
    * @return jsonObject
    * （1）返回一个评论列表，内容包括评论编号、评论用户编号、
    * 评论用户昵称、评论用户头像图片路径、评论发表日期、评论内容。
    * （2）返回总页数
     */
    @RequestMapping("/comment/video")
    public String getVideoComments(@NotNull @RequestParam("videoID") Integer videoID,
                                @NotNull @RequestParam(value = "page") Integer page) throws JSONException {
        return socialContactService.getVideoComments(videoID,page).toString();
    }

    /**
    * 分页获取当前用户评论
    * @param page 页号
    * @param uid 用户编号
    * @return jsonObject
    * （1）返回一个评论列表，内容包含评论编号、评论视频编号、评论视频标题、评论发表日期、评论内容
    * （2）总页数
    */
    @RequestMapping("/comment/user")
    public String getUserComments(@NotNull @RequestParam("page") Integer page,
                               @NotNull @RequestParam("UID") Integer uid) throws JSONException {
        return socialContactService.getUserComments(uid,page).toString();
    }

    /**
    * 关注用户
    * @param map 要关注的用户编号
    * @return jsonObject 返回处理结果
     */
    @RequiresUser
    @RequestMapping(value = "/social/follow")
    public String followUser(@RequestBody HashMap<String,Object> map,
                             HttpServletRequest request) throws JSONException{
        HashMap params = (HashMap) map.get("params");
        Integer uid = (Integer) params.get("UID");
        Users user = (Users) SecurityUtils.getSubject().getPrincipal();
        return socialContactService.followUser(user,uid).toString();
    }

    /**
    * 取消关注
    * @param uid 要取关的用户编号
    * @return jsonObject 返回处理结果
     */
    @RequiresUser
    @RequestMapping(value = "/social/cancelFollow",method = RequestMethod.DELETE)
    public String cancelFollow(@RequestParam("UID") Integer uid) throws JSONException{
        Users user = (Users) SecurityUtils.getSubject().getPrincipal();
        return socialContactService.cancelFollow(user,uid).toString();
    }

    /**
    * 获取用户的已关注用户
    * @return jsonObject 返回一个用户列表，包含用户编号、昵称、头像路径
     */
    @RequiresUser
    @RequestMapping("/social/followed")
    public String getFollowedUser() throws JSONException {
        Users user = (Users) SecurityUtils.getSubject().getPrincipal();
        return socialContactService.getFollowed(user).toString();
    }
}
