package com.project.gamereviewer.service;

import com.project.gamereviewer.dto.request.ProductionCompanyCreateRequest;
import com.project.gamereviewer.dto.response.ProductionCompanyResponse;
import com.project.gamereviewer.entity.CompanyType;
import com.project.gamereviewer.entity.ProductionCompany;
import com.project.gamereviewer.exception.DuplicateResourceException;
import com.project.gamereviewer.exception.ResourceNotFoundException;
import com.project.gamereviewer.mapper.ProductionCompanyMapper;
import com.project.gamereviewer.repository.CompanyTypeRepository;
import com.project.gamereviewer.repository.ProductionCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductionCompanyService {
    
    private final ProductionCompanyRepository productionCompanyRepository;
    private final CompanyTypeRepository companyTypeRepository;
    private final ProductionCompanyMapper productionCompanyMapper;
    
    public Page<ProductionCompanyResponse> getAllCompanies(Pageable pageable) {
        return productionCompanyRepository.findAll(pageable)
            .map(productionCompanyMapper::toResponse);
    }
    
    public ProductionCompanyResponse getCompanyById(Integer id) {
        return productionCompanyRepository.findById(id)
            .map(productionCompanyMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("ProductionCompany", id));
    }
    
    @Transactional
    public ProductionCompanyResponse createCompany(ProductionCompanyCreateRequest request) {
        if (productionCompanyRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("ProductionCompany", "name", request.name());
        }
        
        ProductionCompany company = productionCompanyMapper.toEntity(request);
        
        if (request.companyTypeId() != null) {
            CompanyType companyType = companyTypeRepository.findById(request.companyTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("CompanyType", request.companyTypeId()));
            company.setCompanyType(companyType);
        }
        
        ProductionCompany saved = productionCompanyRepository.save(company);
        return productionCompanyMapper.toResponse(saved);
    }
    
    @Transactional
    public ProductionCompanyResponse updateCompany(Integer id, ProductionCompanyCreateRequest request) {
        ProductionCompany company = productionCompanyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductionCompany", id));
        
        if (!company.getName().equals(request.name()) && productionCompanyRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("ProductionCompany", "name", request.name());
        }
        
        company.setName(request.name());
        company.setFoundedYear(request.foundedYear());
        company.setWebsiteUrl(request.websiteUrl());
        company.setCeo(request.ceo());
        
        if (request.companyTypeId() != null) {
            CompanyType companyType = companyTypeRepository.findById(request.companyTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("CompanyType", request.companyTypeId()));
            company.setCompanyType(companyType);
        } else {
            company.setCompanyType(null);
        }
        
        ProductionCompany updated = productionCompanyRepository.save(company);
        return productionCompanyMapper.toResponse(updated);
    }
    
    @Transactional
    public void deleteCompany(Integer id) {
        if (!productionCompanyRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProductionCompany", id);
        }
        productionCompanyRepository.deleteById(id);
    }
}
