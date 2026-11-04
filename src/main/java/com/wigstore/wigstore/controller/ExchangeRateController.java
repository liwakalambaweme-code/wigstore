package com.wigstore.wigstore.controller;

import com.wigstore.wigstore.dto.ApiDtos.RateRequest;
import com.wigstore.wigstore.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {

    private final ExchangeRateService rateService;

    public ExchangeRateController(ExchangeRateService rateService) {
        this.rateService = rateService;
    }

    @GetMapping
    public Map<String, BigDecimal> today() {
        return Map.of("usdToZmw", rateService.getTodayRate());
    }

    // admin: set today's rate
    @PutMapping
    public Map<String, BigDecimal> set(@RequestBody RateRequest r) {
        return Map.of("usdToZmw", rateService.setTodayRate(r.usdToZmw()).getUsdToZmw());
    }
}