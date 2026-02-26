package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.IProductionCompanyDAO;
import com.project.gamesreviewer.model.ProductionCompany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductionCompanyService {

    private static final Logger logger = LoggerFactory.getLogger(ProductionCompanyService.class);

    @Autowired
    private IProductionCompanyDAO productionCompanyDAO;

    public List<ProductionCompany> getAllCompanies() {
        logger.debug("Fetching all production companies");
        List<ProductionCompany> companies = productionCompanyDAO.findAll();
        companies.sort((c1, c2) -> Integer.compare(c1.id(), c2.id()));
        return companies;
    }

    public ProductionCompany getCompanyById(int id) {
        logger.debug("Fetching production company by id: {}", id);
        return productionCompanyDAO.findById(id);
    }

    public int createCompany(ProductionCompany company) {
        logger.debug("Creating production company: {}", company.name());
        return productionCompanyDAO.create(company);
    }

    public void updateCompany(ProductionCompany company) {
        logger.debug("Updating production company id: {}", company.id());
        productionCompanyDAO.update(company);
    }

    public void deleteCompany(int id) {
        logger.debug("Deleting production company id: {}", id);
        productionCompanyDAO.delete(id);
    }
}
