package pretzel.dreamketcherbe.domain.webtoon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Table(name = "webtoon")
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
    private String thumnail;

    @Column(nullable = false)
    private String description;

    @Column(name = "serialized_status")
    private String serializedStatus;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}
