package com.co.kr.modyeo.api.bbs.repository.custom;

import com.co.kr.modyeo.api.bbs.domain.dto.search.ArticleSearch;
import com.co.kr.modyeo.api.bbs.domain.entity.Article;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ArticleCustomRepository {
    Slice<Article> searchArticle(ArticleSearch articleSearch, PageRequest pageRequest);

    List<Article> findArticleByEmail(String email);
}
