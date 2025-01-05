package com.team2.djavaluxury.service.inter;

import com.team2.djavaluxury.entity.Image;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String save(MultipartFile file);
    byte[] readFileFromLocation(String imageUrl);
    Image create(MultipartFile multipartFile);
    Resource getById(String id);
    void deleteById(String id);
}
