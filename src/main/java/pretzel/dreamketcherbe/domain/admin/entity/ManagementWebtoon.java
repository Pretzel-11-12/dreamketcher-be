package pretzel.dreamketcherbe.domain.admin.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false)
    @ColumnDefault("'NOT_APPROVAL'")
    private String approval;

    @Column(name = "detail_reason")
    private String detailReason;

    @Builder
    public ManagementWebtoon(Webtoon webtoon, Reason reason, String approval, String detailReason) {
        this.webtoon = webtoon;
        this.reason = reason;
        this.approval = approval;
        this.detailReason = detailReason;
    }

    public static ManagementWebtoon addOf(Webtoon webtoon) {
        return ManagementWebtoon.builder()
            .webtoon(webtoon)
            .build();
    }
}
