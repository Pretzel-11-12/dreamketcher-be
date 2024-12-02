package pretzel.dreamketcherbe.domain.webtoon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Table(name = "hash_tag")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashTag extends BaseTimeEntity {

    @Id
    @Column(name = "hash_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

}
