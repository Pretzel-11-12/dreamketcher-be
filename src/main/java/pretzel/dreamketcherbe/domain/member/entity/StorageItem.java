package pretzel.dreamketcherbe.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private StorageFolder storageFolder;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    @Column(nullable = false, name = "is_deleted")
    @ColumnDefault("false")
    private boolean isDeleted;

    @Builder
    private StorageItem(StorageFolder storageFolder, Member member, Webtoon webtoon) {
        this.storageFolder = storageFolder;
        this.member = member;
        this.webtoon = webtoon;
    }

    public static StorageItem create(StorageFolder storageFolder, Member member, Webtoon webtoon) {
        return StorageItem.builder()
            .storageFolder(storageFolder)
            .member(member)
            .webtoon(webtoon)
            .build();
    }
}
