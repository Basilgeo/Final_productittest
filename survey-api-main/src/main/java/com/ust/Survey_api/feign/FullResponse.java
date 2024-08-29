package com.ust.Survey_api.feign;

import com.ust.Survey_api.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FullResponse {
    private Long id;
    private Long surveyid;
    private String requestor;
    public String companyName;
    public Long setId;
    private LocalDate createdDate;
    private LocalDate expireDate;
    public List<SetNameDto> setdata;
}
