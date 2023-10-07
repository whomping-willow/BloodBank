package com.example.bloodbank.DataModels;

public class RequestModel {

    private String reqPhone,reqLocation,reqDivision,reqBlood,reqMsg,reqImage;
    public RequestModel(){

    }

    public RequestModel(String reqPhone, String reqLocation, String reqDivision, String reqBlood, String reqMsg,String reqImage) {
        this.reqPhone = reqPhone;
        this.reqLocation = reqLocation;
        this.reqDivision = reqDivision;
        this.reqBlood = reqBlood;
        this.reqMsg = reqMsg;
        this.reqImage=reqImage;
    }

    public String getReqPhone() {
        return reqPhone;
    }

    public void setReqPhone(String reqPhone) {
        this.reqPhone = reqPhone;
    }

    public String getReqImage() {
        return reqImage;
    }

    public void setReqImage(String reqImage) {
        this.reqImage = reqImage;
    }

    public String getReqLocation() {
        return reqLocation;
    }

    public void setReqLocation(String reqLocation) {
        this.reqLocation = reqLocation;
    }

    public String getReqDivision() {
        return reqDivision;
    }

    public void setReqDivision(String reqDivision) {
        this.reqDivision = reqDivision;
    }

    public String getReqBlood() {
        return reqBlood;
    }

    public void setReqBlood(String reqBlood) {
        this.reqBlood = reqBlood;
    }

    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }


}
