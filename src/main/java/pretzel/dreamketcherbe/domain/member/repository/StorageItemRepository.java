package pretzel.dreamketcherbe.domain.member.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.StorageItem;

public interface StorageItemRepository extends JpaRepository<StorageItem, Long>, StorageItemRepositoryCustom {

    List<StorageItem> findStorageItemByMember(Member member);
}
