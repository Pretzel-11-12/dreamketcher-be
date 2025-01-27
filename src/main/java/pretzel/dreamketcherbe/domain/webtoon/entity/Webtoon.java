package pretzel.dreamketcherbe.domain.webtoon.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.UpdateWebtoonReqDto;

@Table(name = "webtoons")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Webtoon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private String prologue;

    @Column(nullable = false)
    private String story;

    @Column(nullable = false)
    private String description;

    @ColumnDefault("'PRE_SERIES'")
    private String status;

    @ColumnDefault("0.0")
    @Column(nullable = false, name = "average_star")
    private float averageStar;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int episodeCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int interestCount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Builder
    private Webtoon(String title, String thumbnail, String prologue, String story,
        String status,
        String description, Member member, Genre genre) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.prologue = prologue;
        this.story = story;
        this.status = status;
        this.description = description;
        this.member = member;
        this.genre = genre;
    }

    public static Webtoon addOf(CreateWebtoonReqDto dto, Member member, Genre genre,
        ObjectMapper objectMapper) {
        try {
            return Webtoon.builder()
                .title(dto.title())
                .thumbnail(dto.thumbnail())
                .prologue(objectMapper.writeValueAsString(dto.prologue()))
                .story(dto.story())
                .description(dto.description())
                .member(member)
                .genre(genre)
                .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화에 실패하였습니다.", e);
        }
    }

    public void updateOf(UpdateWebtoonReqDto dto, ObjectMapper objectMapper) {
        try {
            this.title = dto.title();
            this.thumbnail = dto.thumbnail();
            this.prologue = objectMapper.writeValueAsString(dto.prologue());
            this.description = dto.description();
            this.story = dto.story();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화에 실패하였습니다.", e);
        }
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void incrementInterestCount(int count) {
        this.interestCount += count;
    }

    public void decrementInterestCount(int count) {
        this.interestCount -= count;
    }
}
