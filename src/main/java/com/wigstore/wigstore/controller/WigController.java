package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.*;
import com.wigstore.wigstore.dto.ApiMapper;
import com.wigstore.wigstore.model.*;
import com.wigstore.wigstore.service.UserService;
import com.wigstore.wigstore.service.WigService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wigs")
public class WigController {

    private final WigService wigService;
    private final UserService userService;
    private final ApiMapper mapper;

    public WigController(WigService wigService, UserService userService, ApiMapper mapper) {
        this.wigService = wigService;
        this.userService = userService;
        this.mapper = mapper;
    }

    // customers: visible wigs only, every filter optional. Prices in filters are USD.
    @GetMapping
    public List<WigResponse> browse(
            @RequestParam(required = false) HairType hairType,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minLength,
            @RequestParam(required = false) Integer maxLength,
            @RequestParam(required = false) Integer density,
            @RequestParam(defaultValue = "USD") Currency currency) {
        return wigService.browse(hairType, color, minPrice, maxPrice, minLength, maxLength, density)
                .stream().map(w -> mapper.toWig(w, currency)).toList();
    }

    @GetMapping("/{id}")
    public WigResponse one(@PathVariable Long id,
                           @RequestParam(defaultValue = "USD") Currency currency) {
        Wig wig = wigService.getWig(id);
        if (!wig.isActive()) {
            throw new IllegalArgumentException("Wig not found");   // hidden wigs look missing
        }
        return mapper.toWig(wig, currency);
    }

    // seller: every wig, including hidden ones
    @GetMapping("/manage")
    public List<WigResponse> manage(@RequestParam(defaultValue = "USD") Currency currency) {
        return wigService.getAllForSeller().stream().map(w -> mapper.toWig(w, currency)).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WigResponse create(@CurrentUserId Long userId, @RequestBody WigRequest r) {
        User seller = userService.getUser(userId);
        return mapper.toWig(wigService.createWig(fromRequest(r), seller), Currency.USD);
    }

    @PutMapping("/{id}")
    public WigResponse update(@PathVariable Long id, @RequestBody WigRequest r) {
        return mapper.toWig(wigService.updateWig(id, fromRequest(r)), Currency.USD);
    }

    // "delete" hides the wig, so old orders keep working
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hide(@PathVariable Long id) {
        wigService.deactivateWig(id);
    }

    private Wig fromRequest(WigRequest r) {
        Wig wig = new Wig();
        wig.setName(r.name());
        wig.setDescription(r.description());
        wig.setPriceUsd(r.priceUsd());
        wig.setColor(r.color());
        wig.setLengthInches(r.lengthInches());
        wig.setDensityPercent(r.densityPercent());
        wig.setHairType(r.hairType());
        wig.setStockQuantity(r.stockQuantity());
        return wig;
    }
}