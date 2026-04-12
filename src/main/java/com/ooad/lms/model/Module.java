package com.ooad.lms.model;

import java.util.ArrayList;
import java.util.List;

public class Module implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long moduleId;
    private String title;
    private final List<Material> materials = new ArrayList<>();

    public Module(Long moduleId, String title) {
        this.moduleId = moduleId;
        this.title = title;
    }

    public void addMaterial(Material material) {
        materials.add(material);
    }

    public Long getModuleId() {
        return moduleId;
    }

    public String getTitle() {
        return title;
    }

    public List<Material> getMaterials() {
        return materials;
    }
}