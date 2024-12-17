package pretzel.dreamketcherbe.domain.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;

@Entity
@Getter
public class ManagementEpisode extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "episode_id")
    private Episode episode;

    @OneToOne
    @JoinColumn(name = "reason_id")
    private Reason reason;
}
