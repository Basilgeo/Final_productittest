package com.ust.Survey_api.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "Assessment",url = "http://localhost:9000/")
public interface AssessmentClient {

     @GetMapping("/assessments/set-id/{setid}")
     public ResponseEntity<List<SetNameDto>> getSet(@PathVariable Long setid);

}
