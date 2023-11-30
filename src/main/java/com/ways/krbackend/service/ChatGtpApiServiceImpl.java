package com.ways.krbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ways.krbackend.DTO.*;
import com.ways.krbackend.model.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import com.ways.krbackend.DTO.ChatRequest;
import com.ways.krbackend.DTO.ChatResponse;
import com.ways.krbackend.DTO.Choice;
import com.ways.krbackend.DTO.Message;

import java.util.ArrayList;
import java.util.List;

import static aj.org.objectweb.asm.Type.getType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class ChatGtpApiServiceImpl implements ChatGtpApiService{
    @Value("${spring.ai.bearer}")
    private String gtpApiKey;
    /*@Value("${spring.ai.bearer}")
    private String bearer;*/

    private final WebClient webClient;


    public ChatGtpApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }


    public List<Choice> chatWithGPT(String message) {
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel("gpt-3.5-turbo");
        List<Message> lstMessages = new ArrayList<>(); //en liste af messages med roller
        lstMessages.add(new Message("system", "You are a helpful assistant."));
        lstMessages.add(new Message("user", message));
        chatRequest.setMessages(lstMessages);
        chatRequest.setN(1); //n er antal svar fra chatgpt
        chatRequest.setTemperature(1); //jo højere jo mere fantasifuldt svar (se powerpoint)
        chatRequest.setMaxTokens(1000); //længde af svar
        chatRequest.setStream(false); //stream = true, er for viderekomne, der kommer flere svar asynkront
        chatRequest.setPresencePenalty(1);

        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(gtpApiKey))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        List<Choice> lst = response.getChoices();

        return lst;
    }

    @Autowired
    private ApplicasionService applicasionService;
    @Override
    public Optional<LinkedList<ApplicationPoints>> validateApplicationsQuiq(String inquiry) {
        String message = "Give me 20 keyword from this inquiry: "+ inquiry+
                ", to find in a resume of a suitable job candidate ";
        List<Choice> lst = chatWithGPT(message);

        String[] wordList = lst.get(0).getMessage().toString().split(" ");
        LinkedList<ApplicationPoints> applPointsList = new LinkedList<>() ;
        List<Application> applications = applicasionService.getApplications();
        for (Application application : applications) {
            int points = 0;
            for (String word : wordList) {
                if(application.getSummery().contains(word)){
                    points++;
                }
            }
            applPointsList.add(new ApplicationPoints(application,points));
        }

        applPointsList.sort(Comparator.comparing(ApplicationPoints::getPoints));
        return Optional.of(applPointsList);
    }

    @Override
    public Optional<List<ApplicationPointsII>> validateApplicationsLong(String inquiry) {
        String message = "Witch of the following candidates matches best to this inquiry: n/"+ inquiry+"n/ Candidates: n/";



        LinkedList<ApplicationPointsII> applPointsList = new LinkedList<>() ;

        List<Application> applications = applicasionService.getApplications();
        for (Application application : applications) {
            message +="applicationId: "+ application.getId() + ": /n" + application.getSummery() +"/n";
        }
        message += "Return me a list of JSON objects with the attributes applicationId, points, reason (short), for the ten best applications,ordered by points given  ";
        List<Choice> lst = chatWithGPT(message);
        Gson gson = new Gson();
        List<ApplicationPointsII> applicationPointsIIList = gson.fromJson(lst.get(0).getMessage().getContent(),new TypeToken<List<ApplicationPointsII>>()->{}.getType());

        //applPointsList.sort(Comparator.comparing(ApplicationPointsII::getPoints));
        return Optional.of(applicationPointsIIList);
    }
}
