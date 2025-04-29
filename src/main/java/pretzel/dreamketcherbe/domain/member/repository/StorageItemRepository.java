package pretzel.dreamketcherbe.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.StorageItem;

public interface StorageItemRepository extends JpaRepository<StorageItem, Long> {

}
