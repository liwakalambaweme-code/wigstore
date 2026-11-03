package com.wigstore.wigstore.service;

import com.wigstore.wigstore.model.Notification;
import com.wigstore.wigstore.model.NotificationType;
import com.wigstore.wigstore.model.User;
import com.wigstore.wigstore.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification notify(User recipient, NotificationType type, String message) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setType(type);
        n.setMessage(message);
        return notificationRepository.save(n);
    }

    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public long unreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndSeenFalse(userId);
    }

    @Transactional
    public void markSeen(Long notificationId, Long userId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!n.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("Notification not found");
        }
        n.setSeen(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllSeen(Long userId) {
        List<Notification> all = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        all.forEach(n -> n.setSeen(true));
        notificationRepository.saveAll(all);
    }
}