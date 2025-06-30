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
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateStorageFolderReqDto;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorageFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isPrivate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private StorageFolder(String name, Boolean isPrivate, Member member) {
        this.name = name;
        this.isPrivate = isPrivate;
        this.member = member;
    }

    public static StorageFolder create(CreateFolderReqDto dto, Member member) {
        return StorageFolder.builder()
            .name(dto.folderName())
            .member(member)
            .isPrivate(dto.isPrivate())
            .build();
    }

    public void update(UpdateStorageFolderReqDto dto) {
        this.name = dto.folderName();
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
