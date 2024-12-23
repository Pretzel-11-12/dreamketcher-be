package pretzel.dreamketcherbe.domain.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentNotRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.NotRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.entity.NotRecommendation;
import pretzel.dreamketcherbe.domain.comment.entity.Recommendation;
import pretzel.dreamketcherbe.domain.comment.entity.Recomment;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentNotRecommendation;
import pretzel.dreamketcherbe.domain.comment.entity.RecommentRecommendation;
import pretzel.dreamketcherbe.domain.comment.exception.CommentException;
import pretzel.dreamketcherbe.domain.comment.exception.CommentExceptionType;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;
import pretzel.dreamketcherbe.domain.comment.repository.NotRecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentNotRecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRecomendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRepository;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;

@Slf4j
@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final EpisodeRepository episodeRepository;
    private final RecommentRepository recommentRepository;
    private final RecommendationRepository recommendationRepository;
    private final NotRecommendationRepository notRecommendationRepository;
    private final RecommentRecomendationRepository recommentRecommendationRepository;
    private final RecommentNotRecommendationRepository recommentNotRecommendationRepository;

    /**
     * 댓글 생성
     */
    @Transactional
    public CreateCommentResDto createComment(Long memberId, Long episodeId,
        CreateCommentReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Comment newComment = Comment
            .builder()
            .member(findMember)
            .episode(findEpisode)
            .content(request.content())
            .build();

        return CreateCommentResDto.of(newComment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long memberId, Long episodeId, Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        findComment.isAuthor(memberId);

        findComment.softDelete();
        commentRepository.save(findComment);
    }

    /**
     * 답글 생성
     */
    @Transactional
    public CreateRecommentResDto createRecomment(Long memberId, Long episodeId, Long commentId,
        CreateRecommentReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        Recomment newRecomment = Recomment
            .builder()
            .member(findMember)
            .episode(findEpisode)
            .content(request.content())
            .parentCommentId(findComment.getId())
            .build();

        return CreateRecommentResDto.of(newRecomment);
    }

    /**
     * 답글 삭제
     */
    @Transactional
    public void deleteRecomment(Long memberId, Long episodeId, Long commentId, Long recommentId) {
        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        findRecomment.isAuthor(memberId);

        findRecomment.softDelete();
        recommentRepository.save(findRecomment);
    }

    /**
     * 댓글 추천
     */
    @Transactional
    public CreateRecommendationResDto recommendComment(Long memberId, Long commentId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        Recommendation newRecommendation = Recommendation
            .builder()
            .member(findMember)
            .comment(findComment)
            .build();
        return CreateRecommendationResDto.of(newRecommendation);
    }

    /**
     * 댓굴 추천 해제
     */
    @Transactional
    public void unrecommendComment(Long memberId, Long commentId, Long recommendationId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        Recommendation findRecommendation = recommendationRepository
            .findByMemberAndComment(memberId, commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENDATION_NOT_FOUND));

        recommendationRepository.delete(findRecommendation);
    }

    /**
     * 댓글 비추천
     */
    @Transactional
    public NotRecommendationResDto notRecommendComment(Long memberId, Long commentId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        NotRecommendation newNotRecommendation = NotRecommendation
            .builder()
            .member(findMember)
            .comment(findComment)
            .build();

        return NotRecommendationResDto.of(newNotRecommendation);
    }

    /**
     * 댓글 비추천 해제
     */
    @Transactional
    public void unnotRecommendComment(Long memberId, Long commentId, Long notRecommendationId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        NotRecommendation findNotRecommendation = notRecommendationRepository
            .findByMemberAndComment(memberId, commentId)
            .orElseThrow(
                () -> new CommentException(CommentExceptionType.NOT_RECOMMENDATION_NOT_FOUND));

        notRecommendationRepository.delete(findNotRecommendation);
    }

    /**
     * 답글 추천
     */
    @Transactional
    public CreateRecommentRecommendationResDto recommendRecomment(Long memberId, Long recommentId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        RecommentRecommendation newRecommentRecommendation = RecommentRecommendation
            .builder()
            .member(findMember)
            .recomment(findRecomment)
            .build();

        return CreateRecommentRecommendationResDto.of(newRecommentRecommendation);
    }

    /**
     * 답글 추천 해제
     */
    @Transactional
    public void unrecommentRecommendation(Long memberId, Long recommentId,
        Long recommentRecommendationId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        RecommentRecommendation findRecommentRecommendation = recommentRecommendationRepository
            .findByMemberAndRecomment(memberId, recommentId)
            .orElseThrow(
                () -> new CommentException(
                    CommentExceptionType.RECOMMENT_RECOMMENDATION_NOT_FOUND));

        recommentRecommendationRepository.delete(findRecommentRecommendation);
    }

    /**
     * 답글 비추천
     */
    @Transactional
    public CreateRecommentNotRecommendationResDto recommentNotRecommendation(Long memberId,
        Long recommentId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        RecommentNotRecommendation newRecommentNotRecommendation = RecommentNotRecommendation
            .builder()
            .member(findMember)
            .recomment(findRecomment)
            .build();

        return CreateRecommentNotRecommendationResDto.of(newRecommentNotRecommendation);
    }

    /**
     * 답글 비추천 해제
     */
    @Transactional
    public void unrecommentNotRecommendation(Long memberId, Long recommentId,
        Long recommentNotRecommendationId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        RecommentNotRecommendation findRecommentNotRecommendation = recommentNotRecommendationRepository
            .findByMemberAndRecomment(memberId, recommentId)
            .orElseThrow(
                () -> new CommentException(
                    CommentExceptionType.RECOMMENT_NOT_RECOMMENDATION_NOT_FOUND));

        recommentNotRecommendationRepository.delete(findRecommentNotRecommendation);
    }
}
