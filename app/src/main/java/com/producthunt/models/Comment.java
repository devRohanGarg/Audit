package com.producthunt.models;

/**
 * Created on 03-05-2017.
 */

public class Comment {

    private long id, child_comments_count, votes;
    private String body;
    private User user;

    public Comment() {
    }

    public long getChild_comments_count() {
        return child_comments_count;
    }

    public void setChild_comments_count(long child_comments_count) {
        this.child_comments_count = child_comments_count;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
