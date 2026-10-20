package com.study.ailawassistant.service;

import com.study.ailawassistant.dto.LawVersionDto;
import com.study.ailawassistant.entity.LawVersion;
import com.study.ailawassistant.repository.LawVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LawVersionService {

    private final LawVersionRepository lawVersionRepository;

    public LawVersionService(LawVersionRepository lawVersionRepository) {
        this.lawVersionRepository = lawVersionRepository;
    }

    public List<LawVersionDto> getVersionsByLawId(Long lawId) {
        return lawVersionRepository.findByLawId(lawId)
                .stream()
                .map(version -> new LawVersionDto(
                        version.getLawSerialNo(),
                        version.getLawId(),
                        version.getPromulgationDate(),
                        version.getEffectiveDate()
                ))
                .toList();
    }
}
