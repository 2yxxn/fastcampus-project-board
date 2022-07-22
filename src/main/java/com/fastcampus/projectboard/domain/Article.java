package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy"),
})
@Entity
public class Article extends AuditingFields {
    // 게시글

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private UserAccount userAccount;        // 유저 정보 (ID)

    @Setter
    @Column(nullable = false)
    private String title;                   // 제목
    @Setter
    @Column(nullable = false, length = 10000)
    private String content;                 // 본문
    @Setter
    // @Column 은 아무 옵션이 없을 때, 생략 가능
    private String hashtag;                 // 해시태그

    // 양방향
    @ToString.Exclude // 순환참조 방지
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    // 생성자
    protected Article() {}

    private Article(UserAccount userAccount, String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    // Id (PK) 에 대한 equals and hashcode
    // Id 는 notnull
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return id != null && id.equals(article.id); // 영속화 되지 않은 entity 는 모두 다른 값으로 보겠다
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
