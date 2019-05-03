package com.chuson.fantazaka;

import java.io.Serializable;


public class Item implements Serializable {
    protected int id;
    protected String url;
    protected String member;
    protected String addedDate;
    protected String lastLotteryDate;
    protected String imageUri;

    public Item(int id, String url, String member, String addedDate, String lastLotteryDate, String imageUri) {
        this.id = id;
        this.url = url;
        this.member = member;
        this.addedDate = addedDate;
        this.lastLotteryDate = lastLotteryDate;
        this.imageUri = imageUri;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getMember() {
        return member;
    }
    public void setMember(String member) {
        this.member = member;
    }

    public String getAddedDate() {
        return addedDate;
    }
    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getLastLotteryDate() {
        return lastLotteryDate;
    }
    public void setLastLotteryDate(String lastLotteryDate) {
        this.lastLotteryDate = lastLotteryDate;
    }

    public String getImageUri() {
        return imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}