package pretzel.dreamketcherbe.domain.member.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;

public interface StorageFolderRepository extends JpaRepository<StorageFolder, Long> {

    List<StorageFolder> findStorageItemByMember(Member member);

    // 공개 폴더만 조회
    List<StorageFolder> findByMemberAndIsPrivatedFalse(Member member);
}
