package com.ways.krbackend.conroller;

import com.ways.krbackend.model.Application;
import com.ways.krbackend.service.ApplicasionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@CrossOrigin
public class ApplicationController {

    @Autowired
    ApplicasionService applicasionService;


    @PostMapping("/application")
    public ResponseEntity<?> postApplication(@RequestBody Application application){
        Optional<Application> response = applicasionService.postApplication(application);
        if(response.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body("saved");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not saved");
        }
    }
}
