package com.bm.controller;

import com.bm.dto.UpdateRequest;
import com.bm.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @PutMapping("/")
    public ResponseEntity<?> update(@RequestBody UpdateRequest updateRequest, @RequestHeader("Authorization") String header) {
        return profileService.update(updateRequest, header);
    }
}
