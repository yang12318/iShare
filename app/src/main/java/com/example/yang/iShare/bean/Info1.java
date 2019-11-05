package com.example.yang.iShare.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class Info1 implements MultiItemEntity {
    private int postId;
    private int userId;
    private int post_userId;
    private int ms_type;
    private String content;
    private String userName;
    private String src;
    private String photo_0;
    private String time;
    private boolean isFollowed;
    public Info1() {

    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPost_userId() {
        return post_userId;
    }

    public void setPost_userId(int post_userId) {
        this.post_userId = post_userId;
    }

    public int getMs_type() {
        return ms_type;
    }

    public void setMs_type(int ms_type) {
        this.ms_type = ms_type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getPhoto_0() {
        return photo_0;
    }

    public void setPhoto_0(String photo_0) {
        this.photo_0 = photo_0;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }

    @Override
    public int getItemType() {
        return ms_type;
    }

}
