package com.bilibili_fake.demo.controller;

import com.bilibili_fake.demo.entity.Users;
import com.bilibili_fake.demo.service.LoginService;
import com.bilibili_fake.demo.service.UsersService;
import com.bilibili_fake.demo.utils.ReadCodeImageUtil;
import com.bilibili_fake.demo.utils.ValidateUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/account")
public class UsersController extends BaseControlller {
    private final LoginService loginService;
    private final UsersService usersService;
    private ValidateUtil validateUtil;
    public UsersController(LoginService loginService, UsersService usersService) {
        this.loginService = loginService;
        this.usersService = usersService;
    }

    /**
    * 验证码获取
    * @param request 用于保存验证码
    * @return jsonObject 返回处理结果，和验证码图片
    */

    @RequiresGuest
    @RequestMapping("/puzzle")
    public String authCode(HttpServletRequest request) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        //获取验证码
        ReadCodeImageUtil readCodeImageUtil = new ReadCodeImageUtil();
        Map<String,String> map = readCodeImageUtil.getValidateCode();
        if(map.isEmpty()){
            jsonObject.put("code",400);
            jsonObject.put("message","获取验证码失败。Σ(っ °Д °;)っ");
            return jsonObject.toString();
        }
        //将验证码存入到Session中进行共享
        HttpSession session = request.getSession();
        session.setAttribute("VALIDATECODE",map.get("code"));

        //调用图片文件生成工具
        jsonObject.put("code",200);
        jsonObject.put("message","验证码获取成功");
        JSONObject json = new JSONObject();
        json.put("imageUrl",map.get("filePath"));
        jsonObject.put("data",json);
        return jsonObject.toString();//若写入成功则返回图片路径
    }

    /**
     * 登陆服务
     * @param username 账号
     * @param password 密码
     * @param puzzleCode 验证码
     * @return jsonObject 返回处理结果
     */
    @RequiresGuest
    @RequestMapping("/login")
    public String login(@NotNull @RequestParam("username") String username,
                        @NotNull @RequestParam("password") String password,
                        @NotNull @RequestParam("puzzleCode") String puzzleCode,
                        HttpServletRequest request) throws JSONException {
        validateUtil = new ValidateUtil();//初始化校验工具
        //获取之前保存的验证码
        HttpSession session = request.getSession();
        String validateCode = session.getAttribute("VALIDATECODE").toString();
        //各项参数校验
        String[] params = new String[3];
        params[0] = username;
        params[1] = password;
        params[2] = puzzleCode;
        //空字符串校验
        if(validateUtil.isParamsEmpty(params)){
            return validateUtil.getErrorInfo("任何一项均不能为空哟。(￣△￣；)");
        }
        //验证码校验
        if(!validateUtil.checkValidateCode(puzzleCode,validateCode)){
            return validateUtil.getErrorInfo("验证码错误，请重新输入验证码。ヽ(*。>Д<)o゜");
        }
        //账号、密码长度校验
        if(!validateUtil.checkStringLength(username)){
            return validateUtil.getErrorInfo("账号长度不符合要求。ヽ(*。>Д<)o゜");
        }
        if (!validateUtil.checkStringLength(password)){
            return validateUtil.getErrorInfo("密码长度不符合要求。ヽ(*。>Д<)o゜");
        }
        return loginService.login(username,password).toString();
    }

    /**
     * 注册服务
     * @param map 包含账号、密码、昵称、邮箱、验证码
     * @parem puzzleCode 验证码
     * @return jsonObject 返回处理结果
     */
    @RequiresGuest
    @RequestMapping(value="/register",method = RequestMethod.POST)
    public String register(@RequestBody HashMap<String,Object> map,
                           HttpServletRequest request) throws JSONException{
        validateUtil = new ValidateUtil();//初始化校验工具
        HashMap params = (HashMap) map.get("params");
        //获取之前保存的验证码
        HttpSession session = request.getSession();
        String validateCode = session.getAttribute("VALIDATECODE").toString();
        //各项参数校验
        String[] paramList = new String[5];
        paramList[0] = params.get("username").toString();
        paramList[1] = params.get("password").toString();
        paramList[2] = params.get("nickname").toString();
        paramList[3] = params.get("email").toString();
        paramList[4] = params.get("puzzleCode").toString();
        //空字符串校验
        if(validateUtil.isParamsEmpty(paramList)){
            return validateUtil.getErrorInfo("任何一项均不能为空哟。(￣△￣；)");
        }
        //验证码校验
        if(!validateUtil.checkValidateCode(paramList[4],validateCode)){
            return validateUtil.getErrorInfo("验证码错误，请重新输入验证码。ヽ(*。>Д<)o゜");
        }
        //账号、密码长度校验
        if(!validateUtil.checkStringLength(paramList[0])){
            return validateUtil.getErrorInfo("账号长度不符合要求。ヽ(*。>Д<)o゜");
        }
        if (!validateUtil.checkStringLength(paramList[1])){
            return validateUtil.getErrorInfo("密码长度不符合要求。ヽ(*。>Д<)o゜");
        }
        return loginService.registerUser(paramList[0],paramList[1],paramList[2],paramList[3]).toString();
    }

    /**
     * 修改用户信息
     * @param map 包含昵称和邮箱
     * @return jsonObject 返回处理结果
     */
    @RequiresUser
    @RequestMapping(value = "/update",method = RequestMethod.PUT)
    public String update(@RequestBody HashMap<String,Object> map) throws JSONException {
        validateUtil = new ValidateUtil();//初始化校验工具
        HashMap paramList = (HashMap) map.get("params");
        //各项参数校验
        String[] params = new String[2];
        params[0] = paramList.get("nickname").toString();
        params[1] = paramList.get("email").toString();
        if(validateUtil.isParamsEmpty(params)){
            return validateUtil.getErrorInfo("任何一项均不能为空哟。(￣△￣；)");
        }
        Users user = (Users) SecurityUtils.getSubject().getPrincipal();
        return usersService.updateUserInfo(user,params[0],params[1]).toString();
    }

    /**
     * 获取用户所有信息
     * @return jsonObject 返回用户编号、用户名、头像图片路径、昵称、邮箱
     */
    @RequestMapping("/info")
    public String info(@RequestParam(value = "UID",required = false) Integer uid) throws JSONException {
        Users user = (Users) SecurityUtils.getSubject().getPrincipal();
        if(uid != null){
            return usersService.getUserInfo(uid).toString();
        }else{
            return usersService.getUserInfo(user.getUserid()).toString();
        }
    }

    /**
     * 修改密码
     * @param map 包含旧密码和新密码
     * @return jsonObject 返回处理结果
     */
    @RequiresUser
    @RequestMapping(value = "/setPassword",method = RequestMethod.PUT)
    public String setPassword(@RequestBody HashMap<String,Object> map) throws JSONException {
        validateUtil = new ValidateUtil();//初始化校验工具
        HashMap paramList = (HashMap) map.get("params");
        //各项参数校验
        String[] params = new String[2];
        params[0] = paramList.get("oldPassword").toString();
        params[1] = paramList.get("newPassword").toString();
        if(validateUtil.isParamsEmpty(params)) {
            return validateUtil.getErrorInfo("任何一项均不能为空哟。(￣△￣；)");
        }
        //新旧密码长度校验
        if (!validateUtil.checkStringLength(params[0]) || !validateUtil.checkStringLength(params[1])){
            return validateUtil.getErrorInfo("密码长度不符合要求，请检查密码长度。ヽ(*。>Д<)o゜");
        }
        Users user = (Users) SecurityUtils.getSubject().getPrincipal();
        if(!validateUtil.checkOldPassword(params[0],user.getPassword())){
            return validateUtil.getErrorInfo("输入的旧密码与当前用户密码不符。ヽ(*。>Д<)o゜");
        }
        return loginService.setPassword(user,params[1]).toString();
    }

    /**
     * 修改头像
     * @param imageData 头像数据
     * @return jsonObject 返回新图片路径
     */
    @RequiresUser
    @RequestMapping(value = "/setAvatar",method = RequestMethod.POST)
    public String setAvatar(MultipartFile imageData) throws JSONException, IOException {
        Users user = (Users) SecurityUtils.getSubject().getPrincipal();
        return usersService.setAvatar(user,imageData).toString();
    }

    /**
     * 重置密码
     * @param username 账号
     * @param secretCode 邮件验证码
     * @param newPassword 新密码
     * @return jsonObject 返回处理结果
     */
    @RequiresGuest
    @RequestMapping("/resetPassword")
    public String resetPassword(@NotNull @RequestParam("username") String username,
                                @NotNull @RequestParam("secretCode") String secretCode,
                                @NotNull @RequestParam("newPassword") String newPassword,
                                HttpServletRequest request) throws JSONException{
        validateUtil = new ValidateUtil();//初始化校验工具
        //各项参数校验
        String[] params = new String[3];
        params[0] = username;
        params[1] = secretCode;
        params[2] = newPassword;
        //获取之前保存的验证码
        HttpSession session = request.getSession();
        String validateCode = session.getAttribute("VALIDATECODE").toString();
        //空字符串校验
        if(validateUtil.isParamsEmpty(params)){
            return validateUtil.getErrorInfo("任何一项均不能为空哟。(￣△￣；)");
        }
        //验证码校验
        if(!validateUtil.checkValidateCode(secretCode,validateCode)){
            return validateUtil.getErrorInfo("验证码错误，请重新输入验证码。ヽ(*。>Д<)o゜");
        }
        //账号、密码长度校验
        if(!validateUtil.checkStringLength(username)){
            return validateUtil.getErrorInfo("账号长度不符合要求。ヽ(*。>Д<)o゜");
        }
        if (!validateUtil.checkStringLength(newPassword)){
            return validateUtil.getErrorInfo("密码长度不符合要求。ヽ(*。>Д<)o゜");
        }
        return loginService.resetPassword(username,newPassword).toString();
    }

    /**
     * 发送邮件验证码
     * @param username 账号
     * @return jsonObject 返回处理结果
     */
    @RequiresGuest
    @RequestMapping("/sendSecretCode")
    public String sendSecretCode(@NotNull @RequestParam("username") String username,
                                 HttpServletRequest request) throws JSONException{
        validateUtil = new ValidateUtil();
        //校验参数
        String[] params = new String[1];
        params[0] = username;
        if(validateUtil.isParamsEmpty(params)){
            return validateUtil.getErrorInfo("用户名不能为空哟。(￣△￣；)");
        }
        //校验用户名长度
        if(!validateUtil.checkStringLength(username)){
            return validateUtil.getErrorInfo("用户名长度不符合要求。ヽ(*。>Д<)o゜");
        }
        //生成验证码
        String randString = "0123456789abcdefghijklmnopqrstuvwxyz";//随机产生数字与字母组合的字符串
        Random random = new Random();
        StringBuilder randomCode = new StringBuilder();
        for(int i = 0; i < 6; i++){
            String rand = String.valueOf(randString.charAt(random.nextInt(randString.length())));
            randomCode.append(rand);
        }
        HttpSession session = request.getSession();
        session.setAttribute("VALIDATECODE",randomCode);
        return loginService.sendCodeByEmail(username,randomCode.toString()).toString();
    }

    /**
     * 注销用户
     * @return jsonObject 返回处理结果
     */
    @RequiresUser
    @RequestMapping("/logout")
    public String logout() throws JSONException {
        return loginService.logout().toString();
    }

    /**
     * 获取用户的统计信息
     * @return jsonObject 返回用户作品数、粉丝数、作品播放数
     */
//    @RequiresUser
    @RequestMapping("/statistic")
    public String statisticInfo(@RequestParam("UID") Integer uid) throws JSONException {
        System.out.println("###333");
        return usersService.getStatistic(uid).toString();
    }
}
