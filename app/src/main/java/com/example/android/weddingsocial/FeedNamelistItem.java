package com.example.android.weddingsocial;
//เป็นคลาสที่ไว้สำหรับโยนข้อมูลไปคลาส FeedNamelistRecyclerAdapter

public class FeedNamelistItem {
    private String UserID;
    private String Firstname;
    private String Lastname;
    private String thumbnail;
    private String Title;

    //GETTER

    public String getUserID() {
        return UserID;
    }

    public String getFirstname() {
        return Firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return Title;
    }

    //SETTER

    public void setUserID (String UserID) {this.UserID = UserID; }

    public void setFirstname(String Firstname) {
        this.Firstname = Firstname;
    }

    public void setLastname(String Lastname) {
        this.Lastname = Lastname;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }
}