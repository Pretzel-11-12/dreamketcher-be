package pretzel.dreamketcherbe.domain.episode.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.UpdateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

import java.time.LocalDate;

@Table(name = "episodes")
@Getter
@Entity
@DynamicInsert
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
    @Setter
    private boolean published;

    @ColumnDefault("0")
    @Column(nullable = false, name = "view_count")
    private Long viewCount;

    @ColumnDefault("0")
    @Column(nullable = false, name = "like_count")
    @Setter
    private int likeCount;

    @ColumnDefault("0.0")
    @Column(nullable = false, name = "average_star")
    private float averageStar;

    @ColumnDefault("'NOT_APPROVAL'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Episode(int no, String title, String thumbnail, List<String> content, String authorNote,
        int likeCount, LocalDate publishedAt,
        Webtoon webtoon, Member member) {
        this.no = no;
        this.title = title;
        this.thumbnail = thumbnail;
        this.content = content;
        this.authorNote = authorNote;
        this.publishedAt = publishedAt;
        this.likeCount = likeCount;
        this.webtoon = webtoon;
        this.member = member;
    }

    public static Episode addOf(CreateEpisodeReqDto dto, int nextEpisodeNo,
        Webtoon webtoon, Member member) {
        return Episode.builder()
            .no(nextEpisodeNo)
            .title(dto.title())
            .thumbnail(dto.thumbnail())
            .content(dto.content())
            .authorNote(dto.authorNote())
            .publishedAt(dto.publishedAt())
            .webtoon(webtoon)
            .member(member)
            .build();
    }

    public void updateOf(UpdateEpisodeReqDto dto) {
        this.title = dto.title();
        this.thumbnail = dto.thumbnail();
        this.content = dto.content();
        this.authorNote = dto.authorNote();
        this.publishedAt = dto.publishedAt();
    }

    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new IllegalStateException(memberId + ", 작성자가 아닙니다.");
        }
    }
}