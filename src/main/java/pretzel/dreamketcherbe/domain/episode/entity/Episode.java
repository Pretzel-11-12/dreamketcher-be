package pretzel.dreamketcherbe.domain.episode.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.episode.dto.CreateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.dto.UpdateEpisodeReqDto;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

import java.time.LocalDate;

@Table(name = "episodes")
@Getter
@Entity
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE episodes SET status = 'DELETED' WHERE id = ?")
@SQLRestriction("status = 'NORMAL'")
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'NORMAL'")
    private EpisodeStatus status;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Episode(int no, String title, String thumbnail, String content, String authorNote,
        int likeCount, LocalDate publishedAt, EpisodeStatus status,
        Webtoon webtoon, Member member) {
        this.no = no;
        this.title = title;
        this.thumbnail = thumbnail;
        this.content = content;
        this.authorNote = authorNote;
        this.publishedAt = publishedAt;
        this.likeCount = likeCount;
        this.status = EpisodeStatus.NORMAL;
        this.webtoon = webtoon;
        this.member = member;
    }

    public static Episode addOf(CreateEpisodeReqDto dto, int nextEpisodeNo,
        Webtoon webtoon, Member member, ObjectMapper objectMapper) throws JsonProcessingException {
        return Episode.builder()
            .no(nextEpisodeNo)
            .title(dto.title())
            .thumbnail(dto.thumbnail())
            .content(objectMapper.writeValueAsString(dto.content()))
            .authorNote(dto.authorNote())
            .publishedAt(dto.publishedAt())
            .webtoon(webtoon)
            .member(member)
            .build();
    }

    public void updateOf(UpdateEpisodeReqDto dto, ObjectMapper objectMapper)
        throws JsonProcessingException {
        this.title = dto.title();
        this.thumbnail = dto.thumbnail();
        this.content = objectMapper.writeValueAsString(dto.content());
        this.authorNote = dto.authorNote();
        this.publishedAt = dto.publishedAt();
    }

    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new EpisodeException(EpisodeExceptionType.UNAUTHORIZED);
        }
    }

    public void updateAverageStar(float averageStar) {
        this.averageStar = averageStar;
    }

    public void softDelete() {
        this.status = EpisodeStatus.DELETED;
    }

    public void report() {
        if (this.status == EpisodeStatus.NORMAL) {
            this.status = EpisodeStatus.REPORTED;
        }
    }

    public void updatePublished(boolean published) {
        this.published = published;
    }

    public void normalize() {
        if (this.status == EpisodeStatus.REPORTED) {
            this.status = EpisodeStatus.NORMAL;
        }
    }

    public boolean incrementLikeCount() {
        int preCount = this.likeCount;
        this.likeCount++;

        return (preCount % 5 != 0 || preCount == 0) && this.likeCount % 5 == 0;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}