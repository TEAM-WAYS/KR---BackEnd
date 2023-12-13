package com.ways.krbackend.controller;

import com.google.api.client.json.Json;
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
    private ApplicationService applicationService;

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

    @GetMapping ("/application/search")
    public ResponseEntity<List<ApplicationPointsTransfer>> searchByInquiry(){
        System.out.println("\n ##--endpoint application/search running--## \n");
        String inq = "Robot Engineer wanted at Tech Solutions A/S! Join our dynamic team, \n" +
                "leveraging your expertise in robotics, programming (C++, Python), \n" +
                "and project management. Contribute to cutting-edge solutions, \n" +
                "troubleshooting complex systems, and driving innovation. \n" +
                "Apply your skills to shape the future of automation. Apply now!";


        System.out.println("inq at endpoint: "+inq);
        Optional<List<ApplicationPointsTransfer>> answer = chatGtpApiService.validateApplicationsLong(inq,10);
        System.out.println("answer : " + answer);
        System.out.println("answer get : " + answer.get());
        if(answer.isPresent()){
            System.out.println("\n ##--endpoint application/search success--## \n");
            System.out.println(" from endpoint returning the following: \n"+answer.get());
            return new ResponseEntity<>(answer.get(),HttpStatus.OK);
            //return ResponseEntity.status(HttpStatus.OK).body(answer.get());
        }
        System.out.println("\n ##--endpoint application/search NOT success !!! --## \n");
        //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(answer.get());
        return new ResponseEntity<>(answer.get(),HttpStatus.NOT_FOUND);
    }

    @GetMapping("/application/testConnection") // VIRKER IKKE
    public String testConnection(){
        return "der er hul igennem";
    }
    @GetMapping("/application/testJSON") // VIRKER IKKE
    public String testJSON(){
        String jsonString = "{name: john, age: 32}";
        return jsonString;
    }
    @GetMapping("/application/testJSONArray") // VIRKER IKKE
    public String testJSONArray(){
        String jsonString = "[{name: john, age: 32},{name: joe, age: 31}]";
        return jsonString;
    }
    @GetMapping("/application/testObject") // VIRKER !!
    public ResponseEntity<Application> testObject(){
        Application application = new Application();
        application.setId(1);
        application.setAge(42);
        application.setName("Per");
        application.setSummary("Super duper");
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @GetMapping("/application/testString") //Virker !!
    public ResponseEntity<String> testString(){
        //String jsonString = "[{name: john, age: 32},{name: joe, age: 31}]";
        String jsonString = "[{\"name\": \"john\", \"age\": 32},{\"name\": \"joe\", \"age\": 31}]";

        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }




}
