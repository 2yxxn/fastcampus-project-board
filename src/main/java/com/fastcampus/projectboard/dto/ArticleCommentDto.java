package com.fastcampus.projectboard.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleCommentDto {
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
    private String content;

    public static ArticleCommentDto of(LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy,String content) {
        return new ArticleCommentDto(createdAt, createdBy, modifiedAt, modifiedBy, content);
    }
}
