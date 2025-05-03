package pretzel.dreamketcherbe.wordfilter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Table(name = "bad_words")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadWord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    @Builder
    public BadWord(String word) {
        this.word = word;
    }
}
