package com.ooad.lms.designpattern.strategy.material;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ooad.lms.model.MaterialType;
import com.ooad.lms.service.FileStorageService;

@Component
public class PdfMaterialStorageStrategy implements MaterialStorageStrategy {
    private final FileStorageService fileStorageService;

    public PdfMaterialStorageStrategy(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public MaterialType supportedType() {
        return MaterialType.PDF;
    }

    @Override
    public FileStorageService.StoredFile store(MultipartFile file) {
        return fileStorageService.storePdf(file);
    }
}
