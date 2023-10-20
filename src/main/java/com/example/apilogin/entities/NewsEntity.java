package com.example.apilogin.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class NewsEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private  Long id;
    private  String source;
    private String title;
    @Column(name = "local_date", columnDefinition = "DATE")
    private LocalDate localDate;
    @Column(columnDefinition = "TEXT")
    private String content;
}