package com.example.simplevideostatusappexamplestatus.Models;

import com.squareup.picasso.RequestCreator;

public class CategoryModels {
    String imagestatus;

    public CategoryModels(String imagestatus) {
        this.imagestatus = imagestatus;
    }

    public CategoryModels(RequestCreator load) {
    }

    public String getImagestatus() {
        return imagestatus;
    }

    public void setImagestatus(String imagestatus) {
        this.imagestatus = imagestatus;
    }
}
