package com.ooad.lms.designpattern.strategy.material;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ooad.lms.exception.BadRequestException;
import com.ooad.lms.model.MaterialType;
import com.ooad.lms.service.FileStorageService;

@Component
public class MaterialStorageStrategyFactory {
    private final Map<MaterialType, MaterialStorageStrategy> strategies = new EnumMap<>(MaterialType.class);

    public MaterialStorageStrategyFactory(List<MaterialStorageStrategy> strategyList) {
        for (MaterialStorageStrategy strategy : strategyList) {
            strategies.put(strategy.supportedType(), strategy);
        }
    }

    public FileStorageService.StoredFile store(MaterialType materialType, MultipartFile file) {
        MaterialStorageStrategy strategy = strategies.get(materialType);
        if (strategy == null) {
            throw new BadRequestException("No storage strategy for material type: " + materialType);
        }
        return strategy.store(file);
    }
}
