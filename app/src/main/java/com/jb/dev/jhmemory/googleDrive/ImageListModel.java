package com.jb.dev.jhmemory.googleDrive;

import android.net.Uri;

public class ImageListModel {
    String name;
    Uri uri;
    public ImageListModel() {
    }

    public ImageListModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
