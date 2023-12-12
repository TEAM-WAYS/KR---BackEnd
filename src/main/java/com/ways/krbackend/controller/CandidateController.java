package com.ways.krbackend.controller;
import com.ways.krbackend.service.CandidateService;
import com.ways.krbackend.model.Candidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
    @RequestMapping("/candidate")
    public class CandidateController {

        @Autowired
        private CandidateService candidateService;

        @GetMapping("/list")
        public ResponseEntity<List<Candidate>> getAllCandidates() {
            List<Candidate> candidates = candidateService.getAllCandidates();
            return ResponseEntity.ok(candidates);
        }

        @GetMapping("/{candidateId}/search")
        public ResponseEntity<Candidate> getCandidateById(@PathVariable Long candidateId) {
            Optional<Candidate> candidate = candidateService.getCandidateById(candidateId);
            return candidate.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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
        @PostMapping("/{candidateId}/move-to-employee")
        public ResponseEntity<String> moveToEmployee(@PathVariable Long candidateId) {
            candidateService.moveCandidateToEmployee(candidateId);
            return ResponseEntity.ok("Candidate moved to Employee list successfully.");
        }

        @DeleteMapping("/{candidateId}")
        public ResponseEntity<String> deleteCandidate(@PathVariable Long candidateId) {
            candidateService.deleteCandidate(candidateId);
            return ResponseEntity.ok("Candidate deleted successfully.");
        }
        @GetMapping("/employees")
        public ResponseEntity<List<Candidate>> getEmployeeCandidates() {
            List<Candidate> employeeCandidates = candidateService.getEmployeeCandidates();
            return ResponseEntity.ok(employeeCandidates);
        }

        @GetMapping("/employees-with-hired-date")
        public ResponseEntity<List<Candidate>> getEmployeesWithHiredDate() {
            List<Candidate> employeesWithHiredDate = candidateService.getEmployeesWithHiredDate();
            return ResponseEntity.ok(employeesWithHiredDate);
        }
    }


