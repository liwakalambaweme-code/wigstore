package com.wigstore.wigstore.service;

import com.wigstore.wigstore.model.Subscriber;
import com.wigstore.wigstore.repository.SubscriberRepository;
import com.wigstore.wigstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final UserRepository userRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, UserRepository userRepository) {
        this.subscriberRepository = subscriberRepository;
        this.userRepository = userRepository;
    }

    // "keep my email for updates": works for guests and for people who have an account
    @Transactional
    public void subscribe(String email, String name) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email is required");
        }
        String clean = email.trim().toLowerCase();

        userRepository.findByEmail(email.trim()).ifPresent(u -> {
            u.setEmailConsent(true);
            userRepository.save(u);
        });

        if (subscriberRepository.findByEmail(clean).isEmpty()) {
            Subscriber s = new Subscriber();
            s.setEmail(clean);
            s.setName(name);
            subscriberRepository.save(s);
        }
    }
}
