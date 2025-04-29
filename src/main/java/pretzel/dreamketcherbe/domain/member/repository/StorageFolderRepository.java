package pretzel.dreamketcherbe.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;

public interface StorageFolderRepository extends JpaRepository<StorageFolder, Long> {

}
