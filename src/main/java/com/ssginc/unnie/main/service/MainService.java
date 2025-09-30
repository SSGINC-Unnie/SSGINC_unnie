package com.ssginc.unnie.main.service;

public interface MainService {

    String getYouTubeVideos();

    void refreshYoutubeCache();

    String callYoutubeApi(String query);
}
