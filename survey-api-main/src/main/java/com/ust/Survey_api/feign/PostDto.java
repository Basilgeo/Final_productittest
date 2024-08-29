package com.ust.Survey_api.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private Long surveyid;
    private String requestor;
    public String companyName;
    public Long setId;
    private LocalDate createdDate;
    public List<SetNameDto> setdata;
}
