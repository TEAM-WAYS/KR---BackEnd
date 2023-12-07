package com.ways.krbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ways.krbackend.DTO.Choice;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.Email;
import com.ways.krbackend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ChatGtpApiService chatGtpApiService;


    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Optional<Application> postApplication(Application application) {
        return Optional.of(applicationRepository.save(application));
    }

    @Override
    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    @Scheduled(fixedRate = 2000000)
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
                        postApplication(application);
                    } else {
                        System.out.println("Failed to parse email to Application");
                    }
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Retrying due to parsing error. Retries left: " + retryCount);

                    try {
                        Thread.sleep(60000);
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

        String jsonString = extractJson(email);

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

    public List<Choice> applicationFromEmail(Email email){
        System.out.println(email.getContent());
        String message = "Analyse this job application: /n"+email.getContent()+" /n " +
                "Create a JSON object with field names 'name', 'age', 'profession', 'title', 'phone', 'summary'/n" +
                "assign Name of applicant to 'name' /n" +
                "assign Age of applicant 'age'/n" +
                "assign Profession 'profession'/n" +
                "assign Title 'title'/n" +
                "assign Phone number 'phone'/n" +
                "assign a short summary of the applicants best qualities to 'summary'/n" +
                "if nothing in the job application matches the criteria leave it empty";
        List<Choice> lst = chatGtpApiService.chatWithGPT(message);
        return lst;
    }
}
