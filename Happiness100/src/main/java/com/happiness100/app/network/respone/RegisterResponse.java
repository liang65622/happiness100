package com.happiness100.app.network.respone;/**
 * Created by Administrator on 2016/8/11.
 */

/**
 * 作者：justin on 2016/8/11 14:46
 */
public class RegisterResponse {

    /**
     * code : 0
     * msg :
     * data : {"headImage":"29.jpeg","nickname":"1111","xf":29,"email":"","sex":0,"zone":"","personSign":"","sessionid":"84826af5-a86d-44f5-94df-b9cc2c6526d7"}
     */

    private int code;
    private String msg;
    /**
     * headImage : 29.jpeg
     * nickname : 1111
     * xf : 29
     * email :
     * sex : 0
     * zone :
     * personSign :
     * sessionid : 84826af5-a86d-44f5-94df-b9cc2c6526d7
     */

    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String headImage;
        private String nickname;
        private int xf;
        private String email;
        private int sex;
        private String zone;
        private String personSign;
        private String sessionid;

        public String getHeadImage() {
            return headImage;
        }

        public void setHeadImage(String headImage) {
            this.headImage = headImage;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getXf() {
            return xf;
        }

        public void setXf(int xf) {
            this.xf = xf;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public String getPersonSign() {
            return personSign;
        }

        public void setPersonSign(String personSign) {
            this.personSign = personSign;
        }

        public String getSessionid() {
            return sessionid;
        }

        public void setSessionid(String sessionid) {
            this.sessionid = sessionid;
        }
    }
}
