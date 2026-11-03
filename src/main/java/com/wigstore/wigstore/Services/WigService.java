package com.wigstore.wigstore.service;

import com.wigstore.wigstore.event.WigCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import com.wigstore.wigstore.model.HairType;
import com.wigstore.wigstore.model.User;
import com.wigstore.wigstore.model.Wig;
import com.wigstore.wigstore.repository.WigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
public class WigService {

    private final WigRepository wigRepository;

    private final ApplicationEventPublisher events;
    
    public WigService(WigRepository wigRepository, ApplicationEventPublisher events) {
    this.wigRepository = wigRepository;
    this.events = events;
}

    // what customers browse: only visible wigs, with optional filters (null = ignore that filter)
    public List<Wig> browse(HairType hairType, String color,
                            BigDecimal minPrice, BigDecimal maxPrice,
                            Integer minLength, Integer maxLength, Integer density) {
        return wigRepository.findByActiveTrue().stream()
                .filter(w -> hairType == null || w.getHairType() == hairType)
                .filter(w -> color == null || w.getColor().equalsIgnoreCase(color))
                .filter(w -> minPrice == null || w.getPriceUsd().compareTo(minPrice) >= 0)
                .filter(w -> maxPrice == null || w.getPriceUsd().compareTo(maxPrice) <= 0)
                .filter(w -> minLength == null || w.getLengthInches() >= minLength)
                .filter(w -> maxLength == null || w.getLengthInches() <= maxLength)
                .filter(w -> density == null || w.getDensityPercent() == density)
                .toList();
    }

    public Wig getWig(Long id) {
        return wigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wig not found"));
    }

    public List<Wig> getAllForSeller() {
        return wigRepository.findAll();   // includes hidden ones
    }

   @Transactional
public Wig createWig(Wig wig, User seller) {
    validate(wig);
    wig.setCreatedBy(seller);
    Wig saved = wigRepository.save(wig);
    events.publishEvent(new WigCreatedEvent(saved));
    return saved;
}

    @Transactional
    public Wig updateWig(Long id, Wig updates) {
        validate(updates);
        Wig wig = getWig(id);
        wig.setName(updates.getName());
        wig.setDescription(updates.getDescription());
        wig.setPriceUsd(updates.getPriceUsd());
        wig.setColor(updates.getColor());
        wig.setLengthInches(updates.getLengthInches());
        wig.setDensityPercent(updates.getDensityPercent());
        wig.setHairType(updates.getHairType());
        wig.setStockQuantity(updates.getStockQuantity());
        return wigRepository.save(wig);
    }

    // "delete" = hide (soft delete), so old orders keep working
    @Transactional
    public void deactivateWig(Long id) {
        Wig wig = getWig(id);
        wig.setActive(false);
        wigRepository.save(wig);
    }

    private void validate(Wig wig) {
        if (wig.getPriceUsd() == null || wig.getPriceUsd().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (wig.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        if (wig.getHairType() == null) {
            throw new IllegalArgumentException("Choose natural or synthetic");
        }
    }
}