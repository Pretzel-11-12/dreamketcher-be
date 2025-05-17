package pretzel.dreamketcherbe.domain.member.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;

public interface StorageFolderRepository extends JpaRepository<StorageFolder, Long> {

    List<StorageFolder> findStorageItemByMember(Member member);
}
