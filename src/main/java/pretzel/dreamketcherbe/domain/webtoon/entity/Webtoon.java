package pretzel.dreamketcherbe.domain.webtoon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;

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

    @ElementCollection
    @Column(nullable = false)
    private List<String> prologue;

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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Webtoon(String title, String thumbnail, List<String> prologue, String story,
        String status,
        String description, Member member) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.prologue = prologue;
        this.story = story;
        this.status = status;
        this.description = description;
        this.member = member;
    }

    public static Webtoon addOf(CreateWebtoonReqDto request, Member member, String thumbnailUrl,
        List<String> prologueUrls) {
        return Webtoon.builder()
            .title(request.title())
            .thumbnail(thumbnailUrl)
            .prologue(prologueUrls)
            .story(request.story())
            .description(request.description())
            .member(member)
            .build();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void updatePrologue(List<String> prologue) {
        this.prologue = prologue;
    }

    public void updateStory(String story) {
        this.story = story;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
