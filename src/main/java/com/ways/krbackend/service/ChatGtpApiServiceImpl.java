package com.ways.krbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ways.krbackend.DTO.*;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.email;
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
    public Optional<LinkedList<ApplicationPoints>> validateApplicationsQuick(String inquiry) {
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
        String message = "Whitch of the following candidates matches best to this inquiry: n/"+ inquiry+"n/ Candidates: n/";



        LinkedList<ApplicationPointsII> applPointsList = new LinkedList<>() ;

        List<Application> applications = applicasionService.getApplications();
        for (Application application : applications) {
            message +="applicationId: "+ application.getId() + ": /n" + application.getSummery() +"/n";
        }
        message += "Return me a list of JSON objects with the attributes applicationId, points, reason (short), " +
                "for the ten best applications,ordered by points given  ";
        List<Choice> lst = chatWithGPT(message);
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            List<ApplicationPointsII> applicationPointsIIList = objectMapper.readValue(lst.get(0).getMessage().getContent(), new TypeReference<List<ApplicationPointsII>>() {
            });
            return Optional.of(applicationPointsIIList);
        }catch (Exception e){
            return Optional.empty();
        }

    }

    @Override
    public Optional<Application> applicationFromEmail(email email){
        String message = "Analyse this job application: /n"+email.getContent()+" /n " +
                "Give me the name the following: /n" +
                "1. Name of applicant/n" +
                "2. Age of applicant/n" +
                "3. Profession/n" +
                "4. Title/n" +
                "5. Phone number/n"+
                "6. A short summery of the applicants best qualities./n" +
                "Return as a Json object with the attributes: 'name', 'age', 'profession', 'title', 'phone', 'summery'";
        List<Choice> lst = chatWithGPT(message);
        ObjectMapper objectMapper= new ObjectMapper();

        try{
            Application application= objectMapper.readValue(lst.get(0).getMessage().getContent(), new TypeReference<Application>() {
            });

            return Optional.of(application);
        }catch (Exception e){
            return Optional.empty();
        }




    }



}
