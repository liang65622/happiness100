package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/12.
 */

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * 作者：jiangsheng on 2016/8/12 09:38
 */
public class AddressData {

    /**
     * addressId : 1
     * receiver : djdjdhdh
     * receiverMobile : 18620376646
     * zone : 安徽省 安庆市 枞阳县
     * address : jsjdj
     * postCode : 246000
     * isDefault : 0
     */
    @Expose
    private List<AddresssBean> addresss;

    public List<AddresssBean> getAddresss() {
        return addresss;
    }

    public void setAddresss(List<AddresssBean> addresss) {
        this.addresss = addresss;
    }

    public static class AddresssBean {
        @Expose
        private int addressId;
        @Expose
        private String receiver;
        @Expose
        private String receiverMobile;
        @Expose
        private String zone;
        @Expose
        private String address;
        @Expose
        private String postCode;
        @Expose
        private int isDefault;

        public int getAddressId() {
            return addressId;
        }

        public void setAddressId(int addressId) {
            this.addressId = addressId;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getReceiverMobile() {
            return receiverMobile;
        }

        public void setReceiverMobile(String receiverMobile) {
            this.receiverMobile = receiverMobile;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPostCode() {
            return postCode;
        }

        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }

        public int getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(int isDefault) {
            this.isDefault = isDefault;
        }
    }
}

