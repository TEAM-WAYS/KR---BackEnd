package com.ways.krbackend.controller;
import com.ways.krbackend.service.CandidateService;
import com.ways.krbackend.model.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

    @RestController
    @RequestMapping("/candidate")
    public class CandidateController {

        @Autowired
        private CandidateService candidateService;

        @GetMapping("/list")
        public String listCandidates(Model model) {
            List<Candidate> candidates = candidateService.getAllCandidates();
            model.addAttribute("candidates", candidates);
            return "list-candidates";
        }
        @GetMapping("/search")
        public String showSearchForm(Model model) {
            model.addAttribute("candidates", new ArrayList<>());
            return "list-candidates";
        }
        @GetMapping("/favorite")
        public String showFavoriteCandidates(Model model, Object favoriteCandidates) {
            model.addAttribute("favoriteCandidates", favoriteCandidates);
            return "list-candidates";
        }
    }
