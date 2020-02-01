package com.bilibili_fake.demo.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ValidateUtil {

    /*
    * 错误信息返回
    * @param errorInfo 错误信息
    * @return 返回错误码和错误信息
     */
    public String getErrorInfo(String errorInfo) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("code",400);
        json.put("message",errorInfo);
        return json.toString();
    }

    /*
    * 校验验证码
    * @param insertCode 用户输入的验证码
    * @param requestCode HttpServlet保存的验证码
    * @return 返回校验是否成功
     */
    public boolean checkValidateCode(String insertCode, String requestCode){
        return requestCode.equalsIgnoreCase(insertCode);
    }

    /*
    * 检查字符串长度
    * @param username
    * @return 校验结果
     */
    public boolean checkStringLength(String words){
        return words.matches("^\\w{8,20}$");
    }

    /*
    * 参数列表是否为空校验
    * @param params 传入的参数列表
    * @return 返回判断结果
     */
    public boolean isParamsEmpty(String[] params){
        for (String i:params) {
            if(i.trim().isEmpty()){
                return true;
            }
        }
        return false;
    }

    /*
    * 旧密码校验
    * @param oldPassword 用户输入的旧密码
    * @param userPassword 用户当前密码
    * @return 返回校验结果
     */
    public boolean checkOldPassword(String oldPassword, String userPassword){
        return userPassword.equalsIgnoreCase(oldPassword);
    }
}
