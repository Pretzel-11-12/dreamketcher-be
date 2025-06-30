package pretzel.dreamketcherbe.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pretzel.dreamketcherbe.common.annotation.Auth;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.CreateFolderResDto;
import pretzel.dreamketcherbe.domain.member.dto.StorageFolderResDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateStorageFolderReqDto;
import pretzel.dreamketcherbe.domain.member.dto.UpdateStorageFolderResDto;
import pretzel.dreamketcherbe.domain.member.service.StorageFolderService;

@RestController
@RequestMapping("/api/v1/storage/folder")
@RequiredArgsConstructor
public class StorageFolderController {

    private final StorageFolderService storageFolderService;

    @GetMapping
    public ResponseEntity<StorageFolderResDto> getFolders(@Auth Long memberId) {
        return ResponseEntity.ok(storageFolderService.getFolders(memberId));
    }

    @GetMapping("/{nickname}")
    public ResponseEntity<StorageFolderResDto> getPublicFolder(
        @PathVariable String nickname) {
        return ResponseEntity.ok(storageFolderService.getFoldersByNickname(nickname));
    }

    @PostMapping
    public ResponseEntity<CreateFolderResDto> createFolder(
        @Auth Long memberId,
        @RequestBody @Valid CreateFolderReqDto createFolderReqDto
    ) {
        return ResponseEntity.ok(storageFolderService.createFolder(memberId, createFolderReqDto));
    }

    @PutMapping("/{folderId}")
    public ResponseEntity<UpdateStorageFolderResDto> updateFolder(
        @PathVariable Long folderId,
        @RequestBody @Valid UpdateStorageFolderReqDto updateStorageFolderReqDto
    ) {
        return ResponseEntity.ok(
            storageFolderService.updateFolder(folderId, updateStorageFolderReqDto));
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long folderId) {
        storageFolderService.deleteFolder(folderId);
        return ResponseEntity.ok().build();
    }
}
