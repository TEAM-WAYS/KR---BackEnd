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
import org.springframework.scheduling.annotation.Scheduled;
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


    private static final ObjectMapper objectMapper = new ObjectMapper();


    public ChatGtpApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/chat/completions").build();
    }


    @Override
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

    private String extractJsonII(String rawContent) {
        int start = rawContent.indexOf('[');
        int end = rawContent.lastIndexOf(']');


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

    }

    List<ApplicationPointsTransfer> parseToDTO(String chatAnswer){
        List<ApplicationPointsTransfer> pointList = new ArrayList();
        int applicationId = 0;
        int points = 0;
        String reason = "";
        String key ="";
        int objectStart = 0;
        int objectEnd = 0;
        int kommaPossition = 0;
        int colonPossition =0;


        boolean hasObjectStart = false, hasObjectEnd = false,hasColon = false, hasKomma= false;



        for(int i =0; i<chatAnswer.length();i++){
            if(chatAnswer.charAt(i) == '{') {
                objectStart=i;
                hasObjectStart = true;
            }

            if(chatAnswer.charAt(i) == '}') {
                objectEnd=i;
                hasObjectEnd =true;

            }
            if(chatAnswer.charAt(i) == '{'||chatAnswer.charAt(i) == ',') {
                kommaPossition=i;
                hasKomma = true;

            }
            if(chatAnswer.charAt(i) == ':') {
               colonPossition=i;
                hasColon = true;
            }

            if(hasObjectStart&&hasObjectEnd && objectStart<objectEnd ) {
                if(hasKomma&&hasColon) {
                    if(kommaPossition<colonPossition) {
                        key = chatAnswer.substring(kommaPossition, colonPossition);
                        hasKomma = false;
                    }else {
                        if (key.contains("applicationId")) {
                            applicationId = Integer.parseInt(chatAnswer.substring( colonPossition+ 1,kommaPossition ).replaceAll("[^\\d]", ""));
                            hasColon = false;
                        } else if(key.contains("points")) {
                            points = Integer.parseInt(chatAnswer.substring( colonPossition+ 1,kommaPossition ).replaceAll("[^\\d]", ""));
                            hasColon = false;
                        }else {
                            reason = chatAnswer.substring(colonPossition+ 1,kommaPossition);
                            pointList.add(new ApplicationPointsTransfer(applicationId,points,reason));
                            hasKomma = false;
                            hasColon = false;
                            hasObjectStart = false;
                            hasObjectEnd = false;
                        }


                    }
                }

            }

        }
     return pointList;
    }

    @Override
    public Optional<List<ApplicationPointsTransfer>> validateApplicationsLong(String inquiry, int noOfApplications) {
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
        String answer = lst.get(0).getMessage().getContent();

        System.out.println("Response from GTP clean: \n ");
        for(ApplicationPointsTransfer a : parseToDTO(answer)){
            System.out.println("id: "+a.getApplicationId());
            System.out.println("points: : "+a.getPoints());
            System.out.println("reason: "+a.getReason());
        }

        return Optional.of(parseToDTO(answer));
    }



    public String removeHtmlTagsII(String htmlString) {
        /*String htmlRegex = "<[^>]*>";
        String plainText = htmlString.replaceAll(htmlRegex, "");*/
        String[] parts = htmlString.split("<html>");
        String plainText = parts[0];
        return plainText;
    }






    public Optional<Application> applicationFromEmailII(Email email){
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
                Optional<Application> response = applicationFromEmailII(email);
                if(response!= null) {
                    //applicasionService.postApplication(response.get());
                }
            }else {
                System.out.println("-Yes");
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    public List<Choice> applicationFromEmail(Email email){
        String from = email.getFromAddress();
        String subject = email.getSubject();
        String plainText = removeHtmlTags(email.getContent());
        System.out.println(plainText);
        String message = "Analyse this job application: /n"+
                "from " + from + "/n" +
                "subject " + subject + "/n" +
                "email content" + plainText + "/n " +
                "Create a JSON object with field names 'name', 'age', 'profession', 'title', 'phone', 'summary'/n" +
                "assign Name of applicant to 'name' /n" +
                "assign Age of applicant 'age'/n" +
                "assign Profession 'profession'/n" +
                "assign Title 'title'/n" +
                "assign Phone number 'phone'/n" +
                "assign a short summary of the applicants best qualities to 'summary'/n" +
                "if nothing in the job application matches the criteria leave it empty";
        List<Choice> lst = chatWithGPT(message);
        return lst;
    }

    @Scheduled(fixedRate = 20000000)
    public void autoSyncApplicants() {
        syncApplicants();
    }

    public void syncApplicants() {
        List<Email> emails = emailService.getEmails();

        for (Email email : emails) {
            int retryCount = 3;

            while (retryCount > 0) {
                try {
                    List<Choice> choices = applicationFromEmail(email);

                    Application application = parseEmail(choices);

                    if (application != null) {
                        application.setEmail(email);
                        applicationService.postApplication(application);
                    } else {
                        System.out.println("Failed to parse email to Application");
                    }
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Retrying due to parsing error. Retries left: " + retryCount);

                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                    }

                    retryCount--;
                }
            }
        }
    }
    public Application parseEmail(List<Choice> list) {
        String email = list.get(0).getMessage().getContent();
        System.out.println("email.getContent" + email);
        String jsonString = extractJson(email);
        System.out.println("jsonString"+jsonString);
        if (jsonString == null || jsonString.isEmpty()) {
            System.out.println("No valid JSON content found. Skipping parsing.");
            return null;
        }
        try {
            return objectMapper.readValue(jsonString, Application.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private String extractJson(String rawContent) {
        int start = rawContent.indexOf('{');
        int end = rawContent.lastIndexOf('}');

        if (start != -1 && end != -1 && start < end) {
            return rawContent.substring(start, end + 1);
        }

        return null;
    }
    public String removeHtmlTags(String htmlString) {
        String[] parts = htmlString.split("<html>", 2);
        String plainText = parts[0];

        return plainText;
    }



}
