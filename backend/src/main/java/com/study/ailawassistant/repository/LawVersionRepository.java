package com.study.ailawassistant.repository;

import com.study.ailawassistant.entity.LawVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LawVersionRepository extends JpaRepository<LawVersion, Long> {

    List<LawVersion> findByLawId(Long lawId);
}
