package com.ways.krbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.ways.krbackend.DTO.ChatRequest;
import com.ways.krbackend.DTO.ChatResponse;
import com.ways.krbackend.DTO.Choice;
import com.ways.krbackend.DTO.Message;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatGtpApiServiceImpl implements ChatGtpApiService{

    private final WebClient webClient;

    @Value("${spring.ai.bearer}")
    private String bearer;

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
        chatRequest.setMaxTokens(300); //længde af svar
        chatRequest.setStream(false); //stream = true, er for viderekomne, der kommer flere svar asynkront
        chatRequest.setPresencePenalty(1);

        ChatResponse response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(bearer))
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();

        List<Choice> lst = response.getChoices();

        return lst;
    }
    /*
    @Override
    public List<Application> validateApplicationsQuick(String inquiry, int noOfApplications) {
        System.out.println("/n ##--validateApplicationsQuick running--## /n");
        String message = "Give me 20 keyword from this inquiry: "+ inquiry+
                ", to find in a resume of a suitable job candidate ";
        List<Choice> lst = chatWithGPT(message);

        System.out.println("/n ##-- from chatGPT: "+lst.get(0).getMessage()+" --## /n");

        String[] wordList = lst.get(0).getMessage().toString().split(" ");
        List<ApplicationPoints> applPointsList = new ArrayList<>() ;


        List<Application> applications = applicationService.getApplications();
        for (Application application : applications) {
            System.out.println("/n looking in appliction: "+application.getId()+" for word: /n");
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

    @Override
    public Optional<List<ApplicationPointsII>> validateApplicationsLong(String inquiry, int noOfApplications) {
        System.out.println("/n ##--validateApplicationsLong running--## /n");
        String message = "Witch of the following candidates matches best to this inquiry: n/"+ inquiry+"n/ Candidates: n/";

        List<Application> applications = validateApplicationsQuick(inquiry,noOfApplications+5);

        //List<Application> applications = applicasionService.getApplications();
        System.out.println("/n --Assessing the "+noOfApplications+" best applications with chatGtp-- /n");
        for (Application application : applications) {
            message +="applicationId: "+ application.getId() + ": /n" + application.getSummary() +"/n";
        }
        message += "Return me a list of JSON objects with the attributes applicationId, points, reason (short), " +
                "for the "+noOfApplications+" best applications,ordered by points given  ";
        System.out.println("Message for for ChatGPT: /n"+message );
        List<Choice> lst = chatWithGPT(message);
        System.out.println("Response from GTP: /n "+lst.get(0).getMessage());
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            System.out.println("Trying to pase respone to a list of objects");
            List<ApplicationPointsII> applicationPointsIIList = objectMapper.readValue(lst.get(0).getMessage().getContent(), new TypeReference<List<ApplicationPointsII>>() {
            });
            /*for(ApplicationPointsII apII : applicationPointsIIList){
                for(Application a : applications){
                    if((a.getId())==(apII.getAppId())){
                        apII.setApplication(a);
                    }
                }
            }
            System.out.println("looking for applications by id");
            applicationPointsIIList.forEach(apII -> {
                applications.stream()
                        .filter(a -> a.getId() == apII.getAppId())
                        .findFirst()
                        .ifPresent(apII::setApplication);
            });
            System.out.println("/n ##--validateApplicationsLong Success--## /n");
            return Optional.of(applicationPointsIIList);
        }catch (Exception e){
            System.out.println("Failed");
            return Optional.empty();
        }

    }

    @Override
    public Optional<Application> applicationFromEmail(Email email){


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
            applicationService.postApplication(application);
            return Optional.of(application);
        }catch (Exception e){
            return Optional.empty();
        }

    }

    @Override
    public ResponseEntity<Object> turnEmailIntoApplication(){
        System.out.println("--endpoint email/transform running--");
        List<Email> emails = emailService.getEmails();
        for(Email email: emails){
            if(email.getApplication() == null) {
                Optional<Application> response = applicationFromEmail(email);
                if(response!= null) {
                    applicationService.postApplication(response.get());
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }
     */
}
