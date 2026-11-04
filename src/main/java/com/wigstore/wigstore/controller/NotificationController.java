package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.NotificationResponse;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ApiMapper mapper;

    public NotificationController(NotificationService notificationService, ApiMapper mapper) {
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<NotificationResponse> mine(@RequestHeader("X-User-Id") Long userId) {
        return notificationService.getForUser(userId).stream().map(mapper::toNotification).toList();
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unread(@RequestHeader("X-User-Id") Long userId) {
        return Map.of("unread", notificationService.unreadCount(userId));
    }

    @PutMapping("/{id}/seen")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void seen(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        notificationService.markSeen(id, userId);
    }

    @PutMapping("/seen-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void seenAll(@RequestHeader("X-User-Id") Long userId) {
        notificationService.markAllSeen(userId);
    }
}