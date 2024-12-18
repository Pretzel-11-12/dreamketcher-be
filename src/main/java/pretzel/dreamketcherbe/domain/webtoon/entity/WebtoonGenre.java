package pretzel.dreamketcherbe.domain.webtoon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Table(name = "webtoon_genres")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WebtoonGenre extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;
}
