package pretzel.dreamketcherbe.domain.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 지정한 에피소드 ID에 해당하며 상태가 'NORMAL'이고 게시된 에피소드의 댓글을 페이지 단위로 조회합니다.
     *
     * @param episodeId 조회할 에피소드의 ID
     * @param pageables 페이징 및 정렬 정보
     * @return 조건에 맞는 댓글의 페이지 객체
     */
    @Query("SELECT c FROM Comment c WHERE c.episode.id = :episodeId AND c.status = 'NORMAL' AND c.episode.published = true ")
    Page<Comment> findByEpisodeId(Long episodeId, Pageable pageables);

    /**
     * 모든 댓글의 ID 목록을 반환합니다.
     *
     * @return 데이터베이스에 존재하는 모든 댓글의 ID 리스트
     */
    @Query("SELECT c.id FROM Comment c")
    List<Long> findAllCommentIds();

    /**
     * 지정한 회원 ID에 해당하며 상태가 'NORMAL'인 댓글 목록을 정렬하여 반환합니다.
     *
     * @param memberId 댓글을 조회할 회원의 ID
     * @param sort 결과 정렬 기준
     * @return 해당 회원의 활성 댓글 리스트
     */
    @Query("SELECT c FROM Comment c WHERE c.member.id = :memberId AND c.status = 'NORMAL'")
    List<Comment> findByMemberIdAndDeletedFalse(@Param("memberId") Long memberId, Sort sort);

    /**
     * 주어진 에피소드 ID 목록에 해당하며, 상태가 'NORMAL'이고 게시된 에피소드의 댓글 ID 목록을 반환합니다.
     *
     * @param episodeIds 댓글을 조회할 에피소드의 ID 목록
     * @return 조건에 맞는 댓글의 ID 목록
     */
    @Query("SELECT c.id FROM Comment c WHERE c.episode.id IN :episodeIds AND c.status = 'NORMAL' AND c.episode.published = true")
    List<Long> findByEpisodeIds(@Param("episodeIds") List<Long> episodeIds);

    /**
     * 지정한 에피소드 ID에 해당하며 상태가 'NORMAL'이고 게시된 에피소드의 댓글 ID 목록을 반환합니다.
     *
     * @param episodeId 댓글을 조회할 에피소드의 ID
     * @return 조건에 맞는 댓글 ID 리스트
     */
    @Query("SELECT c.id FROM Comment c WHERE c.episode.id = :episodeId AND c.status = 'NORMAL' AND c.episode.published = true")
    List<Long> findByEpisodeId(@Param("episodeId") Long episodeId);

    /**
     * 지정된 에피소드 ID 목록에 해당하며 상태가 'NORMAL'이고 에피소드가 공개된 모든 댓글의 상태를 'DELETED'로 일괄 변경합니다.
     *
     * @param episodeIds 상태를 변경할 대상 에피소드의 ID 목록
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.status = 'DELETED' WHERE c.episode.id IN :episodeIds AND c.status = 'NORMAL' AND c.episode.published = true")
    void deleteByEpisodeId(@Param("episodeIds") List<Long> episodeIds);

    /**
     * 지정된 에피소드 ID에 해당하며 상태가 'NORMAL'이고 에피소드가 발행된 모든 댓글의 상태를 'DELETED'로 일괄 변경합니다.
     *
     * @param episodeId 상태를 변경할 대상 에피소드의 ID
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.status = 'DELETED' WHERE c.episode.id = :episodeId AND c.status = 'NORMAL' AND c.episode.published = true")
    void deleteByEpisode(@Param("episodeId") Long episodeId);

}
