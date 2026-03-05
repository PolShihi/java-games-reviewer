package com.project.gamereviewer.service;

import com.project.gamereviewer.dto.response.CompanyTypeResponse;
import com.project.gamereviewer.exception.ResourceNotFoundException;
import com.project.gamereviewer.mapper.CompanyTypeMapper;
import com.project.gamereviewer.repository.CompanyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyTypeService {

    public static final String RESOURSE_NAME = "CompanyType";
    
    private final CompanyTypeRepository companyTypeRepository;
    private final CompanyTypeMapper companyTypeMapper;
    
    public List<CompanyTypeResponse> getAllCompanyTypes() {
        return companyTypeRepository.findAll().stream()
            .map(companyTypeMapper::toResponse)
            .toList();
    }
    
    public CompanyTypeResponse getCompanyTypeById(Integer id) {
        return companyTypeRepository.findById(id)
            .map(companyTypeMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
    }
}
