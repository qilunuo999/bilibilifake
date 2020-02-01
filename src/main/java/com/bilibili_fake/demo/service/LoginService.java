package com.bilibili_fake.demo.service;

import com.bilibili_fake.demo.entity.Users;
import com.bilibili_fake.demo.repository.UsersRepository;
import com.bilibili_fake.demo.utils.EmailSender;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**(
* 该类包含与用户登陆相关的方法
 */
@Service
public class LoginService {
    private final UsersRepository usersRepository;
    private final EmailSender emailSender;

    public LoginService(UsersRepository usersRepository, EmailSender emailSender) {
        this.usersRepository = usersRepository;
        this.emailSender = emailSender;
    }

    /**
    * 用户登陆服务
    * @param username 账号
    * @param password 密码
    * @return 登陆结果
     */
    public JSONObject login(String username, String password) throws JSONException{
        JSONObject json = new JSONObject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            json.put("code",200);
            json.put("message", "登陆成功");
        } catch (UnknownAccountException e) {
            json.put("code",400);
            json.put("message", "用户未注册");
        } catch (IncorrectCredentialsException e) {
            json.put("code",400);
            json.put("message", "密码错误");
        }
        return json;
    }

    /**
    * 用户注册服务
    * @param username 用户名
    * @param password 密码
    * @param nickname 昵称
    * @param email 邮箱
    * @return 返回注册结果
     */
    public JSONObject registerUser(String username, String password, String nickname, String email) throws JSONException{
        JSONObject json = new JSONObject();
        Users user = usersRepository.getUserByUsername(username);
        if(user != null){
            json.put("code",400);
            json.put("message","该账号已存在");
            return json;
        }else{
            user = new Users();
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname);
            user.setEmail(email);
            user.setAvatarurl("/1575168991169.jpg");
            usersRepository.insertUserInfo(user);
            json.put("code",200);
            json.put("message","注册成功。");
        }
        return json;
    }

    /**
    * 修改密码
    * @param newPassword 新密码
    * @param user 用户信息
    * @return 返回密码修改结果
     */
    public JSONObject setPassword(Users user, String newPassword) throws JSONException{
        JSONObject json = new JSONObject();
        usersRepository.updatePassword(user.getUsername(),newPassword);
        json.put("code",200);
        json.put("message","修改密码成功。");
        return json;
    }

    /**
    * 重置密码
    * @param username 账号
    * @param newPassword 新密码
    * @return 返回密码重置结果
     */
    public JSONObject resetPassword(String username, String newPassword) throws JSONException{
        JSONObject json = new JSONObject();
        usersRepository.updatePassword(username,newPassword);
        json.put("code",200);
        json.put("message","重置密码成功");
        return json;
    }

    /**
    * 注销用户
    * @return 返回用户注销结果
     */
    public JSONObject logout() throws JSONException{
        Subject subject = SecurityUtils.getSubject();
        subject.logout();

        JSONObject json = new JSONObject();
        json.put("code",200);
        json.put("result", "成功退出");
        return json;
    }

    /**
    * 向请求邮件验证码的用户发送邮件验证码
    * @param username 用户名
    * @param validateCode 验证码
    * @return 发送验证结果
     */
    public JSONObject sendCodeByEmail(String username, String validateCode) throws JSONException {
        JSONObject json = new JSONObject();
        Users user = usersRepository.getUserByUsername(username);
        if(user == null){
            json.put("code",400);
            json.put("message","您输入的用户不存在。");
            return json;
        }
        emailSender.sendMail(user.getEmail(),"BiliBili_Fake邮箱验证码",validateCode);
        json.put("code",200);
        json.put("message","邮箱验证码发送成功，请打开邮箱查看并输入验证码。");
        return json;
    }
}
