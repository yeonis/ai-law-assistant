package com.study.ailawassistant.service;

import com.study.ailawassistant.dto.LawDto;
import com.study.ailawassistant.repository.LawRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LawService {

    private final LawRepository lawRepository;

    public LawService(LawRepository lawRepository) {
        this.lawRepository = lawRepository;
    }

    public List<LawDto> getLaws(){
        return lawRepository.findAll().stream()//조회한 목록을 하나씩 처리
                .map(law -> new LawDto( //law객체 하나를 LawDto 객체로 변환
                        law.getLawId(),
                        law.getLawName(),
                        law.getLawType()
                ))
                .toList(); //변환된 dto들을 리스트로 모음
    }

    public Optional<LawDto> getLawById(Long id) {

        return lawRepository.findById(id)
                .map(law -> new LawDto(
                        law.getLawId(),
                        law.getLawName(),
                        law.getLawType()
                ));
    }
}

