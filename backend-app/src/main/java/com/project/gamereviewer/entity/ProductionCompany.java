package com.project.gamereviewer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "production_companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(length = 100)
    private String ceo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_type_id")
    private CompanyType companyType;
}
