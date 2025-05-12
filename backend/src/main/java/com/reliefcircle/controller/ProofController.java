package com.reliefcircle.controller;

import com.reliefcircle.model.Charity;
import com.reliefcircle.model.Proof;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.ProofRepository;
import com.reliefcircle.service.AWSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/proofs")
public class ProofController {

    @Autowired
    private AWSService awsService;

    @Autowired
    private ProofRepository proofRepository;

    @Autowired
    private CharityRepository charityRepository;




    /**
     * Uploads a proof document for a charity
     *
     * @param charityId   ID of the charity
     * @param file        The proof document file
     * @param description Optional description for the proof
     * @return ResponseEntity with the result of the upload
     */
    @PostMapping(
        path = "/charity/{charityId}/upload",
        consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProof(
            @PathVariable Long charityId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description
    ) {
        System.out.println("🚀 uploadProof() was triggered");
        System.out.println("File: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println("Description: " + description);

        try {
            Charity charity = charityRepository.findById(charityId)
                    .orElseThrow(() -> new RuntimeException("Charity not found with ID: " + charityId));
            System.out.println("✅ Charity found: " + charity.getName());

            String s3Url = awsService.uploadProofDocument(file, "charity-" + charityId);
            System.out.println("✅ File uploaded to S3: " + s3Url);

            Proof proof = Proof.builder()
                    .charity(charity)
                    .fileUrl(s3Url)
                    .description(description)
                    .build();

            proofRepository.save(proof);
            System.out.println("✅ Proof saved in DB");

            return ResponseEntity.ok("✅ Proof uploaded successfully: " + s3Url);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Upload failed: " + e.getMessage());
        }
    }

    /**
     * Retrieves all proofs for a specific charity
     *
     * @param charityId ID of the charity
     * @return List of proofs for the charity
     */
    @GetMapping("/charity/{charityId}")
    public ResponseEntity<?> getProofsByCharityId(@PathVariable Long charityId) {
        List<Proof> proofs = proofRepository.findByCharityId(charityId);

        // Optional: convert to DTO if you don't want to expose entity
        List<Map<String, Object>> response = proofs.stream().map(proof -> {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("id", proof.getId());
            p.put("fileUrl", proof.getFileUrl());
            p.put("description", proof.getDescription());
            p.put("uploadedAt", proof.getUploadedAt());
            return p;
        }).toList();

        return ResponseEntity.ok(response);
    }

}