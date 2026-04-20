package com.ooad.lms.designpattern.strategy.material;

import org.springframework.web.multipart.MultipartFile;

import com.ooad.lms.model.MaterialType;
import com.ooad.lms.service.FileStorageService;

public interface MaterialStorageStrategy {
    MaterialType supportedType();

    FileStorageService.StoredFile store(MultipartFile file);
}
