package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.SubscribeRequest;
import com.wigstore.wigstore.service.SubscriberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubscribeController {

    private final SubscriberService subscriberService;

    public SubscribeController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/api/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@RequestBody SubscribeRequest r) {
        subscriberService.subscribe(r.email(), r.name());
    }
}