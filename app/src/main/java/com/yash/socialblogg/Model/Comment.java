package com.yash.socialblogg.Model;

import com.google.firebase.database.ServerValue;

public class Comment {

    private String content, uid, uimg, uname;
    private Object timeStamp;

    public Comment() {
    }

    public Comment(String content, String uid, String uimg, String uname) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public Comment(String content, String uid, String uimg, String uname, Object timeStamp) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
