package pretzel.dreamketcherbe.domain.episode.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Table(name = "episodes")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Episode extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, name = "author_note")
    private String authorNote;

    @ColumnDefault("false")
    private boolean published;

    @ColumnDefault("0")
    @Column(name = "view_count")
    private Long viewCount;

    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;
}
