package com.ways.krbackend;

import com.ways.krbackend.DTO.ApplicationPointsTransfer;
import com.ways.krbackend.service.ChatGtpApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatGtpApiServiceImplTest {

    @InjectMocks
    private ChatGtpApiServiceImpl chatGtpApiService;



    @Test
    void testDtoPares(){
        //Arrange
        String chat ="{\"applications\": [{\"applicationId\": \"1\",\"points\": 100,\"reason\":" +
                "\"Matches well with the required skills: robotics, programming (C++, Python), and project management\"}]}";
        List<ApplicationPointsTransfer> expected = new ArrayList<>();
        expected.add(new ApplicationPointsTransfer(1,100,"Matches well with the required skills: robotics, programming (C++, Python), and project management"));
        //Act
        List<ApplicationPointsTransfer> answer=chatGtpApiService.parseToDTO(chat);

        //Assert
        assertEquals(answer,expected);




    }



}
