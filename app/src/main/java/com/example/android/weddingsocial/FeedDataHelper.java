package com.example.android.weddingsocial;

public class FeedDataHelper {

    //for class EventActivity
    private String title;
    private String banner;
    private String date;
    private String key_id;
    private String user_id;


    //for class GreetboxActivity
    private String greetbox_id;
    private String greetbox_post;
    private String Firstname;
    private String Lastname;
    private String thumbnail;


    //for class PostboxActivity
    private String postbox_id;
    private String postbox_post;
    private String thumbnail2;


    // GETTER - SETTER FOR EventActivity

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    // GETTER - SETTER FOR GreetboxActivity

    public String getGreetbox_post() {
        return greetbox_post;
    }

    public void setGreetbox_post(String greetbox_post) {
        this.greetbox_post = greetbox_post;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGreetbox_id() {return greetbox_id; }

    public void setGreetbox_id(String greetbox_id) {
        this.greetbox_id = greetbox_id;
    }


    // GETTER - SETTER FOR PostboxActivity
    public String getPostbox_id() {
        return postbox_id;
    }

    public void setPostbox_id(String postbox_id) {
        this.postbox_id = postbox_id;
    }

    public String getPostbox_post() {
        return postbox_post;
    }

    public void setPostbox_post(String postbox_post) {
        this.postbox_post = postbox_post;
    }

    public String getThumbnail2() {
        return thumbnail2;
    }

    public void setThumbnail2(String thumbnail2) {
        this.thumbnail2 = thumbnail2;
    }
}
