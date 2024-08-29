package com.ust.Survey_api.feign;

import com.ust.Survey_api.model.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequestDto {

    private String requestor;
    private String companyName;
    private Long setid;

}
