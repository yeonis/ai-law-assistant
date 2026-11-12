package com.study.ailawassistant.service;

import com.study.ailawassistant.repository.LawVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LawHistoryImportService {

    private final LawImportService lawImportService;
    private final LawVersionRepository lawVersionRepository;

    public LawHistoryImportService(
            LawImportService lawImportService, LawVersionRepository lawVersionRepository
    ) {
        this.lawImportService = lawImportService;
        this.lawVersionRepository = lawVersionRepository;
    }

    public void importHistory(List<String> mstList){

        for (String mst : mstList) {
            Long lawSerialNo = Long.valueOf(mst);

            if(lawVersionRepository.existsById(lawSerialNo)) {
                System.out.println("이미 저장된 MST: " + mst);
                continue;
            }

            lawImportService.importLaw(mst);
        }
    }
}
