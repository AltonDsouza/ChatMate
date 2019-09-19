package com.dynashwet.chatmate.Model;

/**
 * Created by krupenghetiya on 03/02/17.
 */

public class MyModel {
    private final String image_url;
    private String video_url;
    private final String name;
    private final String type;
    private final String isShare;
    private final String pid;
    private final String caption;
    private final String pic;
    private final String username;
    private final String likes;
    private final String isLike;
    private final String comments;
    private final String uploadby;
    private final String time;
    private final String isFriend;
    private final String isBlock;

    public MyModel(String video_url, String image_url, String name, String type,String isShare, String pid, String caption,
                   String pic,String username, String likes, String isLike, String comments, String uploadby, String time, String isFriend, String isBlock) {
        this.video_url = video_url;
        this.image_url = image_url;
        this.name = name;
        this.type = type;
        this.isShare = isShare;
        this.pid = pid;
        this.caption = caption;
        this.pic = pic;
        this.username = username;
        this.likes = likes;
        this.isLike = isLike;
        this.comments = comments;
        this.uploadby = uploadby;
        this.time = time;
        this.isFriend = isFriend;
        this.isBlock = isBlock;
    }

    public MyModel(String image_url, String name, String type,String isShare, String pid, String caption, String pic,String username,
                   String likes,String isLike, String comments, String uploadby, String time, String isFriend, String isBlock) {
        this.image_url = image_url;
        this.name = name;
        this.type = type;
        this.isShare = isShare;
        this.pid = pid;
        this.caption = caption;
        this.pic = pic;
        this.username = username;
        this.likes = likes;
        this.isLike = isLike;
        this.comments = comments;
        this.uploadby = uploadby;
        this.time = time;
        this.isFriend = isFriend;
        this.isBlock = isBlock;
    }

    public MyModel(String name, String type,String isShare, String pid, String caption, String pic,String username, String likes,String isLike, String comments,
                   String uploadby, String time, String isFriend, String isBlock) {
        this.image_url=null;
        this.name = name;
        this.type = type;
        this.isShare = isShare;
        this.pid = pid;
        this.caption = caption;
        this.pic = pic;
        this.username = username;
        this.likes = likes;
        this.isLike = isLike;
        this.comments = comments;
        this.uploadby = uploadby;
        this.time = time;
        this.isFriend = isFriend;
        this.isBlock = isBlock;
    }


    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getIsLike() {
        return isLike;
    }

    public String getIsBlock() {
        return isBlock;
    }

    public String getIsFriend() {
        return isFriend;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getIsShare() {
        return isShare;
    }

    public String getPid() {
        return pid;
    }

    public String getCaption() {
        return caption;
    }

    public String getPic() {
        return pic;
    }

    public String getUsername() {
        return username;
    }

    public String getLikes() {
        return likes;
    }

    public String getComments() {
        return comments;
    }

    public String getUploadby() {
        return uploadby;
    }

    public String getTime() {
        return time;
    }
}
