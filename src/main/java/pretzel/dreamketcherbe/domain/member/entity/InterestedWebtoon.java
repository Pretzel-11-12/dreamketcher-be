package pretzel.dreamketcherbe.domain.member.entity;

import jakarta.persistence.*;
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
    private Member memberId;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoonId;
}
