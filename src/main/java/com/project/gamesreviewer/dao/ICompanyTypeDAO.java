package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.CompanyType;

import java.util.List;

public interface ICompanyTypeDAO {
    List<CompanyType> findAll();
    CompanyType findById(int id);
}
