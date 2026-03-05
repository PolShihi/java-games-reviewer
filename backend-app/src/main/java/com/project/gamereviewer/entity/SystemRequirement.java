package com.project.gamereviewer.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "system_requirements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"game_id", "system_requirement_type_id"})
})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class SystemRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_requirement_type_id", nullable = false)
    private SystemRequirementType systemRequirementType;

    @Column(name = "storage_gb", nullable = false)
    private Integer storageGb;

    @Column(name = "ram_gb", nullable = false)
    private Integer ramGb;

    @Column(name = "cpu_ghz", precision = 3, scale = 1)
    private BigDecimal cpuGhz;

    @Column(name = "gpu_tflops", precision = 4, scale = 2)
    private BigDecimal gpuTflops;

    @Column(name = "vram_gb")
    private Integer vramGb;
}
