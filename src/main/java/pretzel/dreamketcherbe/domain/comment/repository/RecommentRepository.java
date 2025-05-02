package pretzel.dreamketcherbe.domain.comment.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;

public interface RecommentRepository extends JpaRepository<Recomment, Long> {

    /**
     * 지정된 부모 댓글 ID에 연결된 'NORMAL' 상태의 대댓글을 commentOrder 오름차순으로 페이징하여 조회합니다.
     *
     * @param commentId 부모 댓글의 ID
     * @param pageable 페이징 정보
     * @return 페이징된 'NORMAL' 상태의 대댓글 목록
     */
    @Query("SELECT r FROM Recomment r WHERE r.comment.id = :commentId AND r.status = 'NORMAL' ORDER BY r.commentOrder ASC")
    Page<Recomment> findActiveRecommentsByParentCommentId(Long commentId, Pageable pageable);

    /**
     * 지정된 부모 댓글 ID에 연결된 'NORMAL' 상태의 대댓글 개수를 반환합니다.
     *
     * @param commentId 부모 댓글의 ID
     * @return 'NORMAL' 상태의 대댓글 수
     */
    @Query("SELECT COUNT(r) FROM Recomment r WHERE r.comment.id = :commentId AND r.status = 'NORMAL' ORDER BY r.commentOrder ASC")
    long countByParentCommentIdAndIsDeletedFalse(Long commentId);

    /**
     * 모든 Recomment 엔티티의 ID 목록을 반환합니다.
     *
     * @return 모든 Recomment의 ID 리스트
     */
    @Query("SELECT r.id FROM Recomment r")
    List<Long> findAllRecommentIds();

    /**
     * 주어진 회원 ID에 해당하며 상태가 'NORMAL'인 모든 대댓글을 정렬하여 반환합니다.
     *
     * @param memberId 대댓글을 작성한 회원의 ID
     * @param sort 결과 정렬 기준
     * @return 해당 회원이 작성한 활성 대댓글 목록
     */
    @Query("SELECT r FROM Recomment r WHERE r.member.id = :memberId AND r.status = 'NORMAL'")
    List<Recomment> findByMemberIdAndIsDeletedFalse(@Param("memberId") Long memberId, Sort sort);

    /**
     * 주어진 댓글 ID에 연결된 상태가 'NORMAL'인 모든 대댓글의 ID 목록을 반환합니다.
     *
     * @param commentId 대댓글을 조회할 부모 댓글의 ID
     * @return 해당 댓글에 연결된 활성 대댓글의 ID 리스트
     */
    @Query("SELECT r.id FROM Recomment r WHERE r.comment.id = :commentId AND r.status = 'NORMAL'")
    List<Long> findByComment(@Param("commentId") Long commentId);

    /**
     * 주어진 댓글 ID 목록에 연결된 상태가 'NORMAL'인 대댓글의 ID 목록을 반환합니다.
     *
     * @param commentIds 대댓글을 조회할 댓글 ID 목록
     * @return 상태가 'NORMAL'인 대댓글의 ID 목록
     */
    @Query("SELECT r.id FROM Recomment r WHERE r.comment.id IN :commentIds AND r.status = 'NORMAL'")
    List<Long> findBycommentId(@Param("commentIds") List<Long> commentIds);

    /**
     * 지정된 댓글 ID 목록에 연결된 모든 대댓글의 상태를 'DELETED'로 일괄 변경합니다.
     *
     * @param commentIds 상태를 변경할 대상이 되는 댓글 ID 목록
     */
    @Modifying
    @Query("UPDATE Recomment r SET r.status = 'DELETED' WHERE r.comment.id IN :commentIds")
    void deleteByCommentId(@Param("commentIds") List<Long> commentIds);

    /**
     * 지정된 댓글 ID에 연결된 모든 대댓글의 상태를 'DELETED'로 일괄 변경합니다.
     *
     * @param commentId 상태를 변경할 대상이 되는 부모 댓글의 ID
     */
    @Modifying
    @Query("UPDATE Recomment r SET r.status = 'DELETED' WHERE r.comment.id = :commentId")
    void deleteByComment(@Param("commentId") Long commentId);

}
