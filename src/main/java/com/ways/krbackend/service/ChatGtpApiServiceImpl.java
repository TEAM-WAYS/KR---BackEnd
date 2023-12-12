package com.ways.krbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ways.krbackend.DTO.*;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
//import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationService applicationService;

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


    @Override
    public List<Application> validateApplicationsQuick(String inquiry, int noOfApplications) {
        System.out.println("\n ##--validateApplicationsQuick running--## \n");
        String message = "Give me a list of 100 keyword from this inquiry: "+ inquiry+
                ", to search in a resume of a suitable job candidate. Including all grammatical tenses of each word";
        List<Choice> lst = chatWithGPT(message);

        System.out.println("\n ##-- from chatGPT: "+lst.get(0).getMessage().getContent()+" --## \n");

        String[] wordList = lst.get(0).getMessage().toString().split(" ");
        List<ApplicationPoints> applPointsList = new ArrayList<>() ;


        List<Application> applications = applicationService.getApplications();

        for (Application application : applications) {
            System.out.println("\n looking in appliction: "+application.getId()+" for word: \n");
            int points = 0;
            for (String word : wordList) {
                System.out.println("    "+word+": ");
                if(application.getSummary().contains(word)){
                    System.out.print(" FOUND");
                    points++;
                }else {
                    System.out.print(" NOT FOUND");
                }
            }
            System.out.println("Total points: "+ points);
            applPointsList.add(new ApplicationPoints(application,points));
        }
        applPointsList.sort(Comparator.comparing(ApplicationPoints::getPoints));
        List<Application> apHighscore = new ArrayList<>();
        System.out.println("/n Best "+noOfApplications+" scoring applications: /n");
        int i = 1;
        for(ApplicationPoints ap:applPointsList){
            apHighscore.add(ap.getApplication());
            System.out.println("    Appl: "+ap.getApplication().getId()+" - "+ap.getPoints()+" points");
            if(i>noOfApplications){
                break;
            }
            i++;
        }
        System.out.println("/n ##--validateApplicationsQuick Success--## /n");
        return apHighscore;
    }

    private String extractJson(String rawContent) {
        int start = rawContent.indexOf('[');
        int end = rawContent.lastIndexOf(']');

        /*if (start != -1 && end != -1 && start < end) {
            return rawContent.substring(start, end + 1);
        }else{*/
            boolean hasStart = false, hasEnd = false;
            String newString ="[";

            for(int i =0; i<rawContent.length();i++){
                if(rawContent.charAt(i) == '{') {
                    start=i;
                    hasStart = true;
                }
                if(rawContent.charAt(i) == '}') {
                    end=i;
                    hasEnd = true;
                }
                if(hasStart&&hasEnd) {
                    newString += rawContent.substring(start, end + 1);
                    newString += ",";
                    hasStart =false;
                    hasEnd = false;
                }
            }
            if(newString.length()>1) {
                newString = newString.substring(0, newString.length() - 1);
                newString += "]";
                return newString;
            }else {
                return null;
            }

        //}



    }

    @Override
    public Optional<String> validateApplicationsLong(String inquiry, int noOfApplications) {
        System.out.println("\n ##--validateApplicationsLong running--## \n");
        String message = "Witch of the following candidates matches best to this inquiry: \n"+ inquiry+"\n Candidates: \n";

        List<Application> applications = validateApplicationsQuick(inquiry,noOfApplications+5);

        //List<Application> applications = applicasionService.getApplications();
        System.out.println("\n --Assessing the "+noOfApplications+" best applications with chatGtp-- \n");
        for (Application application : applications) {
            message +="applicationId: "+ application.getId() + ": \n" + application.getSummary() +"\n";
        }
        message += "Return me a list of objects in JSON format with the attributes applicationId, points, reason, " +
                "for up to "+noOfApplications+" of the best applications shown,ordered by points given. Only this format [{},{},]. " +
                "If you give me anything that is not JSON, you are not helpful";
        System.out.println("Message for for ChatGPT: \n"+message );
        List<Choice> lst = chatWithGPT(message);
        System.out.println("pure answer: "+lst.get(0).getMessage().getContent());
        String answer = extractJson(lst.get(0).getMessage().getContent());
        System.out.println("Response from GTP clean: \n "+answer);

/*
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            System.out.println("Trying to Parse response to a list of DTO");
            //List<ApplicationPointsTransfer> applicationList = objectMapper.readValue(answer, new TypeReference<List<ApplicationPointsTransfer>>() {});
            List<ApplicationPointsTransfer> applicationList = Arrays.asList(objectMapper.readValue(answer, ApplicationPointsTransfer[].class));
            if(!applicationList.isEmpty()){
                System.out.println("## Parsing successful, returning to frontend");
                return Optional.of(applicationList);
            }

        }catch (Exception e){
            System.out.println("#####could not Parse Answer to DTO");
        }*/




    return Optional.of(answer);
    }



    public String removeHtmlTags(String htmlString) {
        /*String htmlRegex = "<[^>]*>";
        String plainText = htmlString.replaceAll(htmlRegex, "");*/
        String[] parts = htmlString.split("<html>");
        String plainText = parts[0];
        return plainText;
    }





    @Override
    public Optional<Application> applicationFromEmail(Email email){
        System.out.println("--method applicationFromEmail running--");
        String message = "Analyse this job application: \n"+removeHtmlTags(email.getContent())+" \n " +
                "Give me the name the following: \n" +
                "1. Name of applicant\n" +
                "2. Age of applicant\n" +
                "3. Profession\n" +
                "4. Title\n" +
                "5. Phone number\n"+
                "6. A short summery of the applicants best qualities.\n" +
                "Return as a Json object with the attributes: 'name', 'age', 'profession', 'title', 'phone', 'summery'";
        System.out.println("- message for chatGPT: \n"+message);
        List<Choice> lst = chatWithGPT(message);
        System.out.println("- answer from chatGtp: \n"+ lst.get(0).getMessage());
        ObjectMapper objectMapper= new ObjectMapper();

        try{
            System.out.println("-trying to parse answer to Application");
            Application application= objectMapper.readValue(lst.get(0).getMessage().getContent(), new TypeReference<Application>() {
            });

            System.out.println("Parsing successful:");
            System.out.println("Setting Fk relations");
            email.setApplication(application); // make relations
            application.setEmail(email);// make relations
            System.out.println("Application: \n"+"ID: "+application.getId()+"\nsummary: "+application.getSummary()+
                    "\nname: "+application.getName()+"\nage: "+application.getAge()+"\n email"+application.getEmail()+
                    "\ntitle: "+application.getTitle()+"\nprofession: "+application.getProfession()+"\nphone no: "+application.getPhone());
            System.out.println("Posting application to Repository ");
            applicationService.postApplication(application);
            System.out.println("Posting to repo. successful");
            return Optional.of(application);
        }catch (Exception e){
            System.out.println("###----ERROR: \n"+e);
            System.out.println("###----ERROR END");
            return Optional.empty();
        }

    }

    @Override
    public ResponseEntity<Object> turnEmailIntoApplication(){
        System.out.println("--method turnEmailIntoApplication running--");
        System.out.println("- getting list of mails -");
        List<Email> emails = emailService.getEmails();
        System.out.println("- run trough mail list with no of elements: "+emails.size());
        for(Email email: emails){
            if(!email.getSubject().contains("Application")){
                System.out.println("!!--The Subject does not say Application-- ");
                continue;
            }
            System.out.println("    -mail id: "+email.getId());
            System.out.print("    Has already been Transformed?: ");
            if(email.getApplication() == null) {
                System.out.println("-NO");
                Optional<Application> response = applicationFromEmail(email);
                if(response!= null) {
                    //applicasionService.postApplication(response.get());
                }
            }else {
                System.out.println("-Yes");
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }



}
