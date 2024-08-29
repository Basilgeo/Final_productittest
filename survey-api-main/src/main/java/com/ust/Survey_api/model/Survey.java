package com.ust.Survey_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "survey")
@AllArgsConstructor
@NoArgsConstructor
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyid;
    private long id;
    private String requestor;
    private String companyName;
    private Long setid;
    private LocalDate createdDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "surveyid")
    private List<Emails> emails;


}
