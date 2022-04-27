package com.example.simplevideostatusappexamplestatus.Models;

public class videoModels {
    private String video;
    private String videotitle;
    private String myviews;


    public videoModels(String video, String videotitle) {
        this.video = video;
        this.videotitle = videotitle;
        this.myviews = myviews;
    }

    public videoModels() {
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideotitle() {
        return videotitle;
    }

    public void setVideotitle(String videotitle) {
        this.videotitle = videotitle;
    }

    public String getMyviews() {
        return myviews;
    }

    public void setMyviews(String myviews) {
        this.myviews = myviews;
    }
}

