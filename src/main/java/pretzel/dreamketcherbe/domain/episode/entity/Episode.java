package pretzel.dreamketcherbe.domain.episode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

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

    @ElementCollection
    @Column(nullable = false)
    private List<String> content;

    @Column(nullable = false, name = "author_note")
    private String authorNote;

    @Column(nullable = false, name = "published_at")
    private LocalDate publishedAt;

    @ColumnDefault("false")
    private boolean published;

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
    public Episode(String title, String thumbnail, List<String> content, String authorNote,
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

    public void updateContent(List<String> content) {
        this.content = content;
    }

    public void updateAuthorNote(String authorNote) {
        this.authorNote = authorNote;
    }
}
