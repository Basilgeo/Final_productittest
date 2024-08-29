package com.ust.Survey_api.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetNameDto {
    public Long questionId;
    public String description;
    public List<Answer> answers;
}
