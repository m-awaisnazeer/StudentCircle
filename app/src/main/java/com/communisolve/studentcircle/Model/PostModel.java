package com.communisolve.studentcircle.Model;

public class PostModel {

    String postByUID,imgURL,postText,PostUID;
    Long timeStamp;

    public PostModel() {
    }

    public PostModel(String postByUID, String imgURL, String postText, Long timeStamp) {
        this.postByUID = postByUID;
        this.imgURL = imgURL;
        this.postText = postText;
        this.timeStamp = timeStamp;
    }

    public String getPostByUID() {
        return postByUID;
    }

    public void setPostByUID(String postByUID) {
        this.postByUID = postByUID;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPostUID() {
        return PostUID;
    }

    public void setPostUID(String postUID) {
        PostUID = postUID;
    }
}
