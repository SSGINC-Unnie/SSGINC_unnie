package com.ssginc.unnie.media.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaService {
    String uploadFile(MultipartFile file, String targetType, Long targetId);

    @Transactional(rollbackFor = Exception.class)
    void deleteFile(String fileUrn);

    List<String> getFileUrns(String targetType, long targetId);
}