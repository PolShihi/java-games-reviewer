package com.project.gamereviewer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media_outlets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaOutlet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "founded_year")
    private Integer foundedYear;
}
