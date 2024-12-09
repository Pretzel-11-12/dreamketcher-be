package pretzel.dreamketcherbe.domain.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Table(name = "interested_webtoons")
@Getter
@Entity
@NoArgsConstructor
public class InterestedWebtoon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    @Builder
    public InterestedWebtoon(Member member, Webtoon webtoon) {
        this.member = member;
        this.webtoon = webtoon;
    }
}
