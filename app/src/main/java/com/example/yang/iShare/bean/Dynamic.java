package com.example.yang.iShare.bean;

import java.util.ArrayList;

public class Dynamic {
    private int id;
    private int userId;
    private int post_userId;
    private String introduction;
    private String username;
    private String pub_time;
    private int likes_num;
    private int com_num;
    private String photo0;
    private boolean is_like;
    private boolean is_collect;
    private boolean is_multi;
    private String src;
    private int count;
    private ArrayList<String> photos;
    private ArrayList<String> thumbnails;

    public Dynamic() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPost_userId() {
        return post_userId;
    }

    public void setPost_userId(int post_userId) {
        this.post_userId = post_userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPub_time() {
        return pub_time;
    }

    public void setPub_time(String pub_time) {
        this.pub_time = pub_time;
    }

    public int getLikes_num() {
        return likes_num;
    }

    public void setLikes_num(int likes_num) {
        this.likes_num = likes_num;
    }

    public int getCom_num() {
        return com_num;
    }

    public void setCom_num(int com_num) {
        this.com_num = com_num;
    }

    public String getPhoto0() {
        return photo0;
    }

    public void setPhoto0(String photo0) {
        this.photo0 = photo0;
    }

    public ArrayList<String> getPhotos(){
        return photos;
    }

    public void setPhotos(ArrayList<String> photos){
        this.photos = photos;
    }

    public ArrayList<String> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ArrayList<String> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isIs_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
    }

    public boolean isIs_collect() {
        return is_collect;
    }

    public boolean isIs_multi(){
        return is_multi;
    }

    public void setIs_multi(boolean is_multi){
        this.is_multi = is_multi;
    }

    public void setIs_collect(boolean is_collect) {
        this.is_collect = is_collect;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
