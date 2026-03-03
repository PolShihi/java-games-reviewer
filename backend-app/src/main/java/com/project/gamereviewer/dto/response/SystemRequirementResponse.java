package com.project.gamereviewer.dto.response;

import java.math.BigDecimal;

public record SystemRequirementResponse(
    Integer id,
    Integer gameId,
    SystemRequirementTypeResponse type,
    Integer storageGb,
    Integer ramGb,
    BigDecimal cpuGhz,
    BigDecimal gpuTflops,
    Integer vramGb
) {}
