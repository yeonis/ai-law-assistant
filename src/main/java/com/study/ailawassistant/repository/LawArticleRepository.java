package com.study.ailawassistant.repository;

import com.study.ailawassistant.entity.LawArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LawArticleRepository extends JpaRepository<LawArticle,Long> {
    List<LawArticle> findByLawSerialNoOrderByArticleNoAscArticleBranchNoAsc(Long lawSerialNo);
    List<LawArticle> findByArticleTitleContainingOrArticleContentContaining(String titleKeyword, String contentKeyword);
}
