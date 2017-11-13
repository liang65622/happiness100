package com.justin.utils;/**
 * Created by Administrator on 2016/9/18.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：jiangsheng on 2016/9/18 22:17
 */
public class EmailUtils {

    static EmailUtils mInstance;
    private EmailUtils(){};
    Map<String,String> emailMap;
    public static EmailUtils getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new EmailUtils();
            mInstance.init();
        }
        return mInstance;
    }

    private void init() {
        emailMap = new HashMap<>();
        emailMap.put("qq.com","http://mail.qq.com");
        emailMap.put("gmail.com","http://mail.google.com");
        emailMap.put("sina.com","http://mail.sina.com.cn");
        emailMap.put("163.com","http://mail.163.com");
        emailMap.put("126.com","http://mail.126.com");
        emailMap.put("yeah.net","http://www.yeah.net/");
        emailMap.put("sohu.com","http://mail.sohu.com/");
        emailMap.put("tom.com","http://mail.tom.com/");
        emailMap.put("sogou.com","http://mail.sogou.com/");
        emailMap.put("139.com","http://mail.10086.cn/");
        emailMap.put("hotmail.com","http://www.hotmail.com");
        emailMap.put("live.com","http://login.live.com/");
        emailMap.put("live.cn","http://login.live.cn/");
        emailMap.put("live.com.cn","http://login.live.com.cn");
        emailMap.put("189.com","http://webmail16.189.cn/webmail/");
        emailMap.put("yahoo.com.cn","http://mail.cn.yahoo.com/");
        emailMap.put("yahoo.cn","http://mail.cn.yahoo.com/");
        emailMap.put("eyou.com","http://www.eyou.com/");
        emailMap.put("21cn.com","http://mail.21cn.com/");
        emailMap.put("188.com","http://www.188.com/");
        emailMap.put("foxmail.coom","http://www.foxmail.com");
    }

    public boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public String getEmailAddress(String email)
    {
        int index = email.indexOf("@");
        String findStr = email.substring(index+1,email.length());
        if (emailMap.containsKey(findStr))
        {
            return emailMap.get(findStr);
        }
        return null;
    }


}
