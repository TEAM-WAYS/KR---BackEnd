package com.ways.krbackend.controller;
import com.ways.krbackend.service.CandidateService;
import com.ways.krbackend.model.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        @GetMapping("/favorites")
        public ResponseEntity<List<Candidate>> getFavoriteCandidates() {
            List<Candidate> favoriteCandidates = candidateService.getFavoriteCandidates();
            return ResponseEntity.ok(favoriteCandidates);
        }

        @PostMapping("/{candidateId}/add-to-favorites")
        public ResponseEntity<String> addToFavorites(@PathVariable Long candidateId) {
            candidateService.addToFavorites(candidateId);
            return ResponseEntity.ok("Candidate added to favorites successfully.");
        }
    }

