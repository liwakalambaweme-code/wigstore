
package com.wigstore.wigstore.repository;

import com.wigstore.wigstore.model.WigImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WigImageRepository extends JpaRepository<WigImage, Long> {

    List<WigImage> findByWigIdOrderByDisplayOrderAsc(Long wigId);

    int countByWigId(Long wigId);

    Optional<WigImage> findByWigIdAndPrimaryImageTrue(Long wigId);
}