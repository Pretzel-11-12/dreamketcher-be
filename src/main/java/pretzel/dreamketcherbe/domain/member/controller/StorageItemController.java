package pretzel.dreamketcherbe.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.member.dto.CreateStorageItemReqDto;
import pretzel.dreamketcherbe.domain.member.dto.CreateStorageItemResDto;
import pretzel.dreamketcherbe.domain.member.dto.StorageItemResDto;
import pretzel.dreamketcherbe.domain.member.service.StorageItemService;

@RestController
@RequestMapping("/api/v1/storage/folder/{folderId}/content")
@RequiredArgsConstructor
public class StorageItemController {

    private final StorageItemService storageItemService;

    @GetMapping
    public ResponseEntity<StorageItemResDto> getItems(
        @Auth Long memberId,
        @PathVariable Long folderId
    ) {
        return ResponseEntity.ok(storageItemService.getItems(memberId, folderId));
    }

    // 공개 폴더 조회용
    @GetMapping("/public")
    public ResponseEntity<StorageItemResDto> getItemsPublic(
        @PathVariable Long folderId
    ) {
        return ResponseEntity.ok(storageItemService.getPublicItems(folderId));
    }

    @PostMapping
    public ResponseEntity<CreateStorageItemResDto> createItem(
        @Auth Long memberId,
        @PathVariable Long folderId,
        @RequestBody @Valid CreateStorageItemReqDto createStorageItemReqDto
    ) {
        return ResponseEntity.ok(
            storageItemService.createItem(memberId, folderId, createStorageItemReqDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        storageItemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
}
