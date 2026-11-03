
package com.wigstore.wigstore.repository;

import com.wigstore.wigstore.model.HairType;
import com.wigstore.wigstore.model.Wig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.util.List;

public interface WigRepository extends JpaRepository<Wig, Long> {

    List<Wig> findByActiveTrue();

    List<Wig> findByActiveTrueAndHairType(HairType hairType);

    List<Wig> findByActiveTrueAndPriceUsdBetween(BigDecimal min, BigDecimal max);

    List<Wig> findByActiveTrueAndColorIgnoreCase(String color);
}