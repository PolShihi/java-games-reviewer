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


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SystemRequirementService {

    public static final String RESOURSE_NAME = "SystemRequirement";
    public static final String SYSTEM_REQUIREMENT_FOR_SAME_GAME_WITH_SAME_TYPE_EXCEPTION_MESSAGE = "System requirement for this game and type already exists";
    
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
            throw new ResourceNotFoundException(GameService.RESOURSE_NAME, gameId);
        }
        return systemRequirementRepository.findByGameId(gameId, pageable)
            .map(systemRequirementMapper::toResponse);
    }
    
    public SystemRequirementResponse getSystemRequirementById(Integer id) {
        return systemRequirementRepository.findById(id)
            .map(systemRequirementMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
    }
    
    @Transactional
    public SystemRequirementResponse createSystemRequirement(SystemRequirementCreateRequest request) {
        if (systemRequirementRepository.existsByGameIdAndSystemRequirementTypeId(
                request.gameId(), request.systemRequirementTypeId())) {
            throw new DuplicateResourceException(
                SYSTEM_REQUIREMENT_FOR_SAME_GAME_WITH_SAME_TYPE_EXCEPTION_MESSAGE
            );
        }
        
        Game game = gameRepository.findById(request.gameId())
            .orElseThrow(() -> new ResourceNotFoundException(GameService.RESOURSE_NAME, request.gameId()));
        
        SystemRequirementType type = systemRequirementTypeRepository.findById(request.systemRequirementTypeId())
            .orElseThrow(() -> new ResourceNotFoundException(SystemRequirementTypeService.RESOURSE_NAME, request.systemRequirementTypeId()));
        
        SystemRequirement systemRequirement = systemRequirementMapper.toEntity(request);
        systemRequirement.setGame(game);
        systemRequirement.setSystemRequirementType(type);
        
        SystemRequirement saved = systemRequirementRepository.save(systemRequirement);
        return systemRequirementMapper.toResponse(saved);
    }
    
    @Transactional
    public SystemRequirementResponse updateSystemRequirement(Integer id, SystemRequirementCreateRequest request) {
        SystemRequirement systemRequirement = systemRequirementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
        
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
            throw new ResourceNotFoundException(RESOURSE_NAME, id);
        }
        systemRequirementRepository.deleteById(id);
    }
}
