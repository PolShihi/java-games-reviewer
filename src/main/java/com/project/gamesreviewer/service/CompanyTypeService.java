package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.ICompanyTypeDAO;
import com.project.gamesreviewer.model.CompanyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyTypeService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyTypeService.class);

    @Autowired
    private ICompanyTypeDAO companyTypeDAO;

    public List<CompanyType> getAllCompanyTypes() {
        logger.debug("Fetching all company types");
        List<CompanyType> types = companyTypeDAO.findAll();
        types.sort((t1, t2) -> Integer.compare(t1.id(), t2.id()));
        return types;
    }

    public CompanyType getCompanyTypeById(int id) {
        logger.debug("Fetching company type by id: {}", id);
        return companyTypeDAO.findById(id);
    }
}
