package com.ways.krbackend.controller;

import com.ways.krbackend.DTO.ApplicationPoints;
import com.ways.krbackend.DTO.ApplicationPointsTransfer;
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

    @Autowired
    private ChatGtpApiService chatGtpApiService;


    @PostMapping("/application")
    public ResponseEntity<?> postApplication(@RequestBody Application application){
        Optional<Application> response = applicationService.postApplication(application);
        if(response.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body("saved");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not saved");
        }
    }

    @PostMapping("/application/search")
    public String searchByInquiry(@RequestBody String inq){
        System.out.println("\n ##--endpoint application/search running--## \n");
        Optional<String> answer = chatGtpApiService.validateApplicationsLong(inq,10);

        if(answer.isPresent()){
            System.out.println("\n ##--endpoint application/search success--## \n");
            System.out.println(" from endpoint returning the following: \n"+answer.get());
            return answer.get();
            //return ResponseEntity.status(HttpStatus.OK).body(answer.get());
        }
        System.out.println("\n ##--endpoint application/search NOT success !!! --## \n");
        //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(answer.get());
        return  null;
    }
}
