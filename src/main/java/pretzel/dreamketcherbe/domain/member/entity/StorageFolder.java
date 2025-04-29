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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateStorageFolderReqDto;

@Entity
@Getter
@SQLDelete(sql = "UPDATE storage_folder SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorageFolder extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, name = "is_deleted")
    @ColumnDefault("false")
    private boolean isDeleted;

    @Builder
    private StorageFolder(String name, Member member) {
        this.name = name;
        this.member = member;
    }

    public static StorageFolder create(CreateFolderReqDto dto, Member member) {
        return StorageFolder.builder()
            .name(dto.name())
            .member(member)
            .build();
    }

    public void update(UpdateStorageFolderReqDto dto) {
        this.name = dto.name();
    }

    public void delete() {
        if (!this.isDeleted) {
            this.isDeleted = true;
        }
    }
}
