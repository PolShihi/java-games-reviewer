package com.project.gamereviewer.service;

import com.project.gamereviewer.dto.request.SystemRequirementCreateRequest;
import com.project.gamereviewer.dto.response.SystemRequirementResponse;
import com.project.gamereviewer.entity.Game;
import com.project.gamereviewer.entity.SystemRequirement;
import com.project.gamereviewer.entity.SystemRequirementType;
import com.project.gamereviewer.exception.DuplicateResourceException;
import com.project.gamereviewer.exception.ResourceNotFoundException;
import com.project.gamereviewer.mapper.SystemRequirementMapper;
import com.project.gamereviewer.repository.GameRepository;
import com.project.gamereviewer.repository.SystemRequirementRepository;
import com.project.gamereviewer.repository.SystemRequirementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SystemRequirementService {
    
    private final SystemRequirementRepository systemRequirementRepository;
    private final GameRepository gameRepository;
    private final SystemRequirementTypeRepository systemRequirementTypeRepository;
    private final SystemRequirementMapper systemRequirementMapper;
    
    public Page<SystemRequirementResponse> getAllSystemRequirements(Pageable pageable) {
        return systemRequirementRepository.findAll(pageable)
            .map(systemRequirementMapper::toResponse);
    }
    
    public Page<SystemRequirementResponse> getSystemRequirementsByGameId(Integer gameId, Pageable pageable) {
        if (!gameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game", gameId);
        }
        return systemRequirementRepository.findByGameId(gameId, pageable)
            .map(systemRequirementMapper::toResponse);
    }
    
    public SystemRequirementResponse getSystemRequirementById(Integer id) {
        return systemRequirementRepository.findById(id)
            .map(systemRequirementMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("SystemRequirement", id));
    }
    
    @Transactional
    public SystemRequirementResponse createSystemRequirement(SystemRequirementCreateRequest request) {
        if (systemRequirementRepository.existsByGameIdAndSystemRequirementTypeId(
                request.gameId(), request.systemRequirementTypeId())) {
            throw new DuplicateResourceException(
                "System requirement for this game and type already exists"
            );
        }
        
        Game game = gameRepository.findById(request.gameId())
            .orElseThrow(() -> new ResourceNotFoundException("Game", request.gameId()));
        
        SystemRequirementType type = systemRequirementTypeRepository.findById(request.systemRequirementTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("SystemRequirementType", request.systemRequirementTypeId()));
        
        SystemRequirement systemRequirement = systemRequirementMapper.toEntity(request);
        systemRequirement.setGame(game);
        systemRequirement.setSystemRequirementType(type);
        
        SystemRequirement saved = systemRequirementRepository.save(systemRequirement);
        return systemRequirementMapper.toResponse(saved);
    }
    
    @Transactional
    public SystemRequirementResponse updateSystemRequirement(Integer id, SystemRequirementCreateRequest request) {
        SystemRequirement systemRequirement = systemRequirementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("SystemRequirement", id));
        
        systemRequirement.setStorageGb(request.storageGb());
        systemRequirement.setRamGb(request.ramGb());
        systemRequirement.setCpuGhz(request.cpuGhz());
        systemRequirement.setGpuTflops(request.gpuTflops());
        systemRequirement.setVramGb(request.vramGb());
        
        SystemRequirement updated = systemRequirementRepository.save(systemRequirement);
        return systemRequirementMapper.toResponse(updated);
    }
    
    @Transactional
    public void deleteSystemRequirement(Integer id) {
        if (!systemRequirementRepository.existsById(id)) {
            throw new ResourceNotFoundException("SystemRequirement", id);
        }
        systemRequirementRepository.deleteById(id);
    }
}
