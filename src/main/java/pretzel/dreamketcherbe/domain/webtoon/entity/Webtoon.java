package pretzel.dreamketcherbe.domain.webtoon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.member.entity.Member;

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

    @ColumnDefault("'NOT_APPROVAL'")
    private String approval;

    @ColumnDefault("'PRE_SERIES'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Webtoon(String title, String thumbnail, String prologue, String story,
        String description, String approval, String status, Member member) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.prologue = prologue;
        this.story = story;
        this.description = description;
        this.approval = approval;
        this.status = status;
        this.member = member;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void updatePrologue(String prologue) {
        this.prologue = prologue;
    }

    public void updateStory(String story) {
        this.story = story;
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
