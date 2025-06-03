package com.github.lemongrab32.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "articles")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(name = "short_description")
    @JsonProperty("short_description")
    private String shortDescription;
    private String author;
    private String content;
    @Column(name = "publishing_date")
    @JsonProperty("publishing_date")
    private Date publishingDate;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tags;

}
