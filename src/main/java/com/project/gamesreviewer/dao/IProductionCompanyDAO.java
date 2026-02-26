package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.ProductionCompany;

import java.util.List;

public interface IProductionCompanyDAO {
    List<ProductionCompany> findAll();
    ProductionCompany findById(int id);
    int create(ProductionCompany company);
    void update(ProductionCompany company);
    void delete(int id);
}
