package com.project.gamesreviewer.model;

public record SystemRequirement(
    int id,
    int gameId,
    int systemRequirementTypeId,
    int storageGb,
    int ramGb,
    Double cpuGhz,
    Double gpuTflops,
    Integer vramGb,
    String requirementType,
    String gameTitle
) {
    public SystemRequirement {
        if (storageGb <= 0) {
            throw new IllegalArgumentException("Storage must be greater than 0 GB");
        }
        if (ramGb <= 0) {
            throw new IllegalArgumentException("RAM must be greater than 0 GB");
        }
        if (cpuGhz != null && cpuGhz <= 0) {
            throw new IllegalArgumentException("CPU GHz must be greater than 0");
        }
        if (gpuTflops != null && gpuTflops <= 0) {
            throw new IllegalArgumentException("GPU TFlops must be greater than 0");
        }
    }
    
    public SystemRequirement(int id, int gameId, int systemRequirementTypeId, 
                           int storageGb, int ramGb, Double cpuGhz, Double gpuTflops, Integer vramGb) {
        this(id, gameId, systemRequirementTypeId, storageGb, ramGb, cpuGhz, gpuTflops, vramGb, null, null);
    }
    
    public SystemRequirement(int id, int gameId, int systemRequirementTypeId, 
                           int storageGb, int ramGb, Double cpuGhz, Double gpuTflops, Integer vramGb,
                           String requirementType) {
        this(id, gameId, systemRequirementTypeId, storageGb, ramGb, cpuGhz, gpuTflops, vramGb, requirementType, null);
    }
    
    public SystemRequirement withRequirementType(String requirementType) {
        return new SystemRequirement(id, gameId, systemRequirementTypeId, storageGb, ramGb, 
                                    cpuGhz, gpuTflops, vramGb, requirementType, gameTitle);
    }
    
    public SystemRequirement withGameTitle(String gameTitle) {
        return new SystemRequirement(id, gameId, systemRequirementTypeId, storageGb, ramGb, 
                                    cpuGhz, gpuTflops, vramGb, requirementType, gameTitle);
    }
}
