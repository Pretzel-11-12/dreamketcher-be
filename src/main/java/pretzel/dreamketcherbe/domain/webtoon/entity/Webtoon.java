package pretzel.dreamketcherbe.domain.webtoon.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.webtoon.dto.CreateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.dto.UpdateWebtoonReqDto;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;

@Table(name = "webtoons")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE webtoons SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
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

    @ColumnDefault("'PRE_SERIES'")
    private String status;

    @ColumnDefault("0.0")
    @Column(nullable = false, name = "average_star")
    private float averageStar;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int episodeCount;

    @Column(nullable = false, name = "is_deleted")
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int interestCount;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @OneToMany(mappedBy = "webtoon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebtoonTag> webtoonTags = new ArrayList<>();

    @Builder
    private Webtoon(String title, String thumbnail, String prologue, String story,
        String status, Member member, Genre genre) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.prologue = prologue;
        this.story = story;
        this.status = status;
        this.member = member;
        this.genre = genre;
    }

    public static Webtoon addOf(CreateWebtoonReqDto dto, Member member, Genre genre) {
        return Webtoon.builder()
            .title(dto.title())
            .thumbnail(dto.thumbnail())
            .story(dto.story())
            .member(member)
            .genre(genre)
            .build();
    }

    public void updateOf(UpdateWebtoonReqDto dto) {
        this.title = dto.title();
        this.thumbnail = dto.thumbnail();
        this.story = dto.story();
    }

    public void isAuthor(Long memberId) {
        if (!member.getId().equals(memberId)) {
            throw new WebtoonException(WebtoonExceptionType.UNAUTORIZED_MEMBER);
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

    public void softDelete() {
        if (!this.isDeleted) {
            this.isDeleted = true;
        }
    }

    public void incrementEpisodeCount(int count) {
        this.episodeCount = count;
    }

    public void addTag(Tag tag) {
        WebtoonTag wt = new WebtoonTag(this, tag);
        webtoonTags.add(wt);
        tag.getWebtoonTags().add(wt);
    }

    public void removeTag(Tag tag) {
        webtoonTags.removeIf(wt -> wt.getTag().equals(tag));
        tag.getWebtoonTags().removeIf(wt -> wt.getWebtoon().equals(this));
    }
}
