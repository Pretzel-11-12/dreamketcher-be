package pretzel.dreamketcherbe.domain.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.webtoon.entity.SerializationPeriod;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Entity
@Getter
public class ManagementWebtoon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    @OneToOne
    @JoinColumn(name = "reason_id")
    private Reason reason;

    @OneToOne
    @JoinColumn(name = "serialization_id")
    private SerializationPeriod serializationPeriod;
}
