package com.wigstore.wigstore.service;

import com.wigstore.wigstore.model.Currency;
import com.wigstore.wigstore.model.ExchangeRate;
import com.wigstore.wigstore.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository rateRepository;

    public ExchangeRateService(ExchangeRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    // today's rate, or the most recent one we have if today's isn't set yet
    public BigDecimal getTodayRate() {
        return rateRepository.findByRateDate(LocalDate.now())
                .or(rateRepository::findTopByOrderByRateDateDesc)
                .map(ExchangeRate::getUsdToZmw)
                .orElseThrow(() -> new IllegalStateException("No exchange rate has been set yet"));
    }

    @Transactional
    public ExchangeRate setTodayRate(BigDecimal usdToZmw) {
        if (usdToZmw == null || usdToZmw.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rate must be greater than zero");
        }
        ExchangeRate rate = rateRepository.findByRateDate(LocalDate.now())
                .orElseGet(ExchangeRate::new);
        rate.setRateDate(LocalDate.now());
        rate.setUsdToZmw(usdToZmw);
        return rateRepository.save(rate);
    }

    // used when displaying prices: dollars stay as they are, kwacha = dollars x today's rate
    public BigDecimal convert(BigDecimal usdAmount, Currency target) {
        if (target == Currency.USD) {
            return usdAmount;
        }
        return usdAmount.multiply(getTodayRate()).setScale(2, RoundingMode.HALF_UP);
    }
}
