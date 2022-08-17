package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    // 게시글 리스트 조회
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        // 검색어가 없을 경우
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable) // Entity 로 리턴
                    .map(ArticleDto::from); // Entity -> Dto
        }

        // 검색어가 있을 경우
        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable)
                    .map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable)
                    .map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable)
                    .map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable)
                    .map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable)
                    .map(ArticleDto::from);
        };
    }

    // 게시글 조회
    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() ->
                        new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId)
                ); // Optional 까기
    }

    // 게시글 저장
    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity()); // Dto -> Entity 로 바꿔 저장
    }

    // 게시글 수정
    public void updateArticle(ArticleDto dto) {
        try {
            // 내부의 값을 필요로 하지는 않고, 다른 객체에게 할당하는 목적으로만 조회
            // getReferenceById : 탐색 결과가 없으면 내부에서 exception 발생
            Article article = articleRepository.getReferenceById(dto.id());

            if (dto.title() != null) { article.setTitle(dto.title()); }
            if (dto.content() != null) { article.setContent(dto.content()); }
            article.setHashtag(dto.hashtag()); // null 가능
        }
        catch (EntityNotFoundException e) { // 없는 게시글의 수정 정보를 입력
            // 경고 로그를 찍고 아무 것도 하지 않는다
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다 - dto: {}", dto);
        }

        // class 단위로 transaction 이 묶여있기 때문에 따로 save 를 날릴 필요는 없음
        // articleRepository.save(article);
    }

    // 게시글 삭제
    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    // 게시글 수 반환
    public long getArticleCount() {
        return articleRepository.count();
    }
}
