package pretzel.dreamketcherbe.domain.episode.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

import java.time.LocalDate;

@Table(name = "episodes")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Episode extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int no;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, name = "author_note")
    private String authorNote;

    @Column(nullable = false, name = "published_at")
    private LocalDate publishedAt;

    @ColumnDefault("false")
    private boolean published;

    @ColumnDefault("'NOT_APPROVAL'")
    private String status;

    @ColumnDefault("0")
    @Column(name = "view_count")
    private int viewCount;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Episode(String title, String thumbnail, String content, String authorNote,
        Webtoon webtoon, Member member) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.content = content;
        this.authorNote = authorNote;
        this.webtoon = webtoon;
        this.member = member;
    }

    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new IllegalStateException(memberId + ", 작성자가 아닙니다.");
        }
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateAuthorNote(String authorNote) {
        this.authorNote = authorNote;
    }
}
