package pretzel.dreamketcherbe.domain.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Entity
@Getter
public class Reason extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;
}
