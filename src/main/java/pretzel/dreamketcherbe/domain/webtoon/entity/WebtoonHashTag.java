package pretzel.dreamketcherbe.domain.webtoon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Table(name = "webtoon_hash_tag")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebtoonHashTag extends BaseTimeEntity {

    @Id
    @Column(name = "webtoon_hash_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hash_tag_id")
    private HashTag hashTag;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;
}
