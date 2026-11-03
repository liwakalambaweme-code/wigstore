package com.wigstore.wigstore.event;
import com.wigstore.wigstore.model.Order;
public record OrderPlacedEvent(Order order) {}