package com.ways.krbackend.controller;

import com.ways.krbackend.DTO.ApplicationPointsII;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.service.ApplicationService;
import com.ways.krbackend.service.ChatGtpApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;


    @PostMapping("/application")
    public ResponseEntity<?> postApplication(@RequestBody Application application){
        Optional<Application> response = applicationService.postApplication(application);
        if(response.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body("saved");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not saved");
        }
    }
/*
    @PostMapping("/application/search")
    public List<ApplicationPointsII> searchByInquiry(@RequestParam String inquiry){
        System.out.println("/n ##--endpoint application/search running--## /n");
        Optional<List<ApplicationPointsII>> applicationPointsList = chatGtpApiService.validateApplicationsLong(inquiry,10);
        if(applicationPointsList!=null){
            System.out.println("/n ##--endpoint application/search success--## /n");
            return applicationPointsList.get();
        }
        System.out.println("/n ##--endpoint application/search NOT success !!! --## /n");
        return null;
    }

 */
}
