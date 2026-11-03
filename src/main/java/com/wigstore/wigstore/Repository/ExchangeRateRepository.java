package com.wigstore.wigstore.repository;

import com.wigstore.wigstore.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByRateDate(LocalDate date);

    Optional<ExchangeRate> findTopByOrderByRateDateDesc();
}