package com.project.gamereviewer.service;

import com.project.gamereviewer.dto.response.SystemRequirementTypeResponse;
import com.project.gamereviewer.exception.ResourceNotFoundException;
import com.project.gamereviewer.mapper.SystemRequirementTypeMapper;
import com.project.gamereviewer.repository.SystemRequirementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SystemRequirementTypeService {

    public static final String RESOURSE_NAME = "SystemRequirementType";
    
    private final SystemRequirementTypeRepository systemRequirementTypeRepository;
    private final SystemRequirementTypeMapper systemRequirementTypeMapper;
    
    public List<SystemRequirementTypeResponse> getAllSystemRequirementTypes() {
        return systemRequirementTypeRepository.findAll().stream()
            .map(systemRequirementTypeMapper::toResponse)
            .toList();
    }
    
    public SystemRequirementTypeResponse getSystemRequirementTypeById(Integer id) {
        return systemRequirementTypeRepository.findById(id)
            .map(systemRequirementTypeMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
    }
}
