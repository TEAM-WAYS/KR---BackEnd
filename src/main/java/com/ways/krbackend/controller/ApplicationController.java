package com.ways.krbackend.controller;

import com.ways.krbackend.DTO.ApplicationPoints;
import com.ways.krbackend.DTO.ApplicationPointsII;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.service.ApplicasionService;
import com.ways.krbackend.service.ChatGtpApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
public class ApplicationController {

    @Autowired
    ApplicasionService applicasionService;

    @Autowired
    private ChatGtpApiService chatGtpApiService;


    @PostMapping("/application")
    public ResponseEntity<?> postApplication(@RequestBody Application application){
        Optional<Application> response = applicasionService.postApplication(application);
        if(response.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body("saved");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not saved");
        }
    }

    @GetMapping("/application/search")
    public List<ApplicationPointsII> searchByInquiry(@RequestParam String inquiry){
        System.out.println("--endpoint application/search running--");
        Optional<List<ApplicationPointsII>> applicationPointsList = chatGtpApiService.validateApplicationsLong(inquiry,10);
        if(applicationPointsList!=null){
            System.out.println("--endpoint application/search success--");
            return applicationPointsList.get();
        }
        return null;
    }
}
