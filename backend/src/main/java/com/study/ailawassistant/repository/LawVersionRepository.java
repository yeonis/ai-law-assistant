package com.study.ailawassistant.repository;

import com.study.ailawassistant.entity.LawVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LawVersionRepository extends JpaRepository<LawVersion, Long> {

    List<LawVersion> findByLawId(Long lawId);

    Optional<LawVersion> findFirstByLawIdAndEffectiveDateLessThanOrderByEffectiveDateDescLawSerialNoDesc(
            Long lawId,
            LocalDate effectiveDate
    );
}
