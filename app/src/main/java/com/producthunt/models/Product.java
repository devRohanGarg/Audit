package com.producthunt.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 03-05-2017.
 */

public class Product implements Parcelable {

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    private String name, tag_line, day, discussion_url, redirect_url, screenshot_url, thumbnail_url;
    private long id, comments_count, votes_count;

    public Product() {
    }

    protected Product(Parcel in) {
        name = in.readString();
        tag_line = in.readString();
        day = in.readString();
        discussion_url = in.readString();
        redirect_url = in.readString();
        screenshot_url = in.readString();
        thumbnail_url = in.readString();
        id = in.readLong();
        comments_count = in.readLong();
        votes_count = in.readLong();
    }

    public long getComments_count() {
        return comments_count;
    }

    public void setComments_count(long comments_count) {
        this.comments_count = comments_count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag_line() {
        return tag_line;
    }

    public void setTag_line(String tag_line) {
        this.tag_line = tag_line;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDiscussion_url() {
        return discussion_url;
    }

    public void setDiscussion_url(String discussion_url) {
        this.discussion_url = discussion_url;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String getScreenshot_url() {
        return screenshot_url;
    }

    public void setScreenshot_url(String screenshot_url) {
        this.screenshot_url = screenshot_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVotes_count() {
        return votes_count;
    }

    public void setVotes_count(long votes_count) {
        this.votes_count = votes_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(tag_line);
        parcel.writeString(day);
        parcel.writeString(discussion_url);
        parcel.writeString(redirect_url);
        parcel.writeString(screenshot_url);
        parcel.writeString(thumbnail_url);
        parcel.writeLong(id);
        parcel.writeLong(comments_count);
        parcel.writeLong(votes_count);
    }
}
