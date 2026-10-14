package com.study.ailawassistant.service;

import com.study.ailawassistant.dto.LawDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LawService {
    public List<LawDto> getLaws(){
        return List.of(
                new LawDto(1L, "관세법", "관세"),
                new LawDto(2L, "전자정부법", "행정"),
                new LawDto(3L, "개인정보 보호법", "정보보호")
        );
    }

    public Optional<LawDto> getLawById(Long id) {

        return getLaws().stream()
                .filter(law -> law.id().equals(id))
                .findFirst();
    }
}

