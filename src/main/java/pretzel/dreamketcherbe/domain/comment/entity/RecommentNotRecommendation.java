package pretzel.dreamketcherbe.domain.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.domain.member.entity.Member;

@Table(name = "recomment_not_recommendations")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommentNotRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;

    @JoinColumn(name = "recomment_id")
    @ManyToOne
    private Recomment recomment;

    @Builder
    public RecommentNotRecommendation(Member member, Recomment recomment) {
        this.member = member;
        this.recomment = recomment;
    }

}
