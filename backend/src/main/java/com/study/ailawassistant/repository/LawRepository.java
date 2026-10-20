package com.study.ailawassistant.repository;

import com.study.ailawassistant.entity.Law;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LawRepository extends JpaRepository<Law, Long> {
}

