package com.ssginc.unnie.media.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    String uploadFile(MultipartFile file, String targetType, long targetId);

    @Transactional(rollbackFor = Exception.class)
    void deleteFile(String fileUrn);
}
