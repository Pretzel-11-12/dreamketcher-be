package pretzel.dreamketcherbe.domain.comment.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.common.dto.PageReqDto;
import pretzel.dreamketcherbe.common.dto.PageResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentNotRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateRecommentResDto;
import pretzel.dreamketcherbe.domain.comment.dto.NotRecommendationResDto;
import pretzel.dreamketcherbe.domain.comment.dto.RecommentResDto;
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
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonException;
import pretzel.dreamketcherbe.domain.webtoon.exception.WebtoonExceptionType;
import pretzel.dreamketcherbe.domain.webtoon.repository.WebtoonRepository;

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
    private final RedisTemplate<String, String> redisTemplate;

    private static final String RECOMMEND_SET_KEY_PREFIX = "comment:recommend:";
    private static final String RECOMMEND_COUNT_KEY_PREFIX = "comment:recommendCount:";
    private static final String NOT_RECOMMEND_SET_KEY_PREFIX = "comment:notRecommend:";
    private static final String NOT_RECOMMEND_COUNT_KEY_PREFIX = "comment:notRecommendCount:";

    private static final String RECOMMENT_RECOMMEND_SET_KEY_PREFIX = "recomment:recommend:";
    private static final String RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX = "recomment:recommendCount:";
    private static final String RECOMMENT_NOT_RECOMMEND_SET_KEY_PREFIX = "recomment:notRecommend:";
    private static final String RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX = "recomment:notRecommendCount:";

    private static final String RECOMMEND_LUA_SCRIPT = """
        if redis.call('sismember', KEYS[1], ARGV[1]) == 1 then
            redis.call('srem', KEYS[1], ARGV[1])
            redis.call('decr', KEYS[2])
            return -1
        else
            redis.call('sadd', KEYS[1], ARGV[1])
            redis.call('incr', KEYS[2])
            return 1
        end
        """;
    private final RedisScript<Long> recommendScript = new DefaultRedisScript<>(RECOMMEND_LUA_SCRIPT,
        Long.class);

    private static final String NOT_RECOMMEND_LUA_SCRIPT = """
        if redis.call('sismember', KEYS[1], ARGV[1]) == 1 then
            redis.call('srem', KEYS[1], ARGV[1])
            redis.call('decr', KEYS[2])
            return -1
        else
            redis.call('sadd', KEYS[1], ARGV[1])
            redis.call('incr', KEYS[2])
            return 1
        end
        """;
    private final RedisScript<Long> notRecommendScript = new DefaultRedisScript<>(
        NOT_RECOMMEND_LUA_SCRIPT, Long.class);


    /**
     * 댓글 생성
     */
    @Transactional
    public CreateCommentResDto createComment(Long memberId, Long webtoonId, Long episodeId,
        CreateCommentReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Comment newComment = Comment.addOf(request, findMember, findEpisode);
        commentRepository.save(newComment);

        return CreateCommentResDto.of(newComment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long memberId, Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        findComment.isAuthor(memberId);

        findComment.softDelete();
        commentRepository.save(findComment);
    }

    /**
     * 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResDto<CommentResDto> getComments(Long episodeId, PageReqDto pageReqDto) {
        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Pageable pageable = PageRequest.of(
            pageReqDto.getPage(),
            pageReqDto.getSize(),
            Sort.by(Sort.Direction.fromString(pageReqDto.getOrder()), "createdAt")
        );

        Page<Comment> comments = commentRepository.findByEpisodeId(episodeId,
            pageable);

        return new PageResDto<>(
            comments.getContent().stream()
                .map(CommentResDto::of)
                .toList(),
            comments.getTotalElements()
        );
    }

    /**
     * 답글 생성
     */
    @Transactional
    public CreateRecommentResDto createRecomment(Long memberId, Long webtoonId, Long episodeId,
        Long commentId,
        CreateRecommentReqDto request) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        int commentOrder =
            (int) recommentRepository.countByParentCommentIdAndIsDeletedFalse(findComment.getId())
                + 1;

        Recomment newRecomment = Recomment.addOf(request, commentOrder, findMember, findEpisode,
            findComment);
        recommentRepository.save(newRecomment);

        int childCommentCount = (int) recommentRepository.countByParentCommentIdAndIsDeletedFalse(
            findComment.getId());

        findComment.updateChildCommentCount(childCommentCount);
        commentRepository.save(findComment);

        return CreateRecommentResDto.of(newRecomment);
    }

    /**
     * 답글 삭제
     */
    @Transactional
    public void deleteRecomment(Long memberId, Long commentId, Long recommentId) {
        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        findRecomment.isAuthor(memberId);

        findRecomment.softDelete();
        recommentRepository.save(findRecomment);

        int childCommentCount = (int) recommentRepository.countByParentCommentIdAndIsDeletedFalse(
            findComment.getId());
        findComment.updateChildCommentCount(childCommentCount);
        commentRepository.save(findComment);
    }

    /**
     * 답글 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResDto<RecommentResDto> getRecomments(Long episodeId, Long commentId,
        PageReqDto pageReqDto) {
        Episode findEpisode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new EpisodeException(EpisodeExceptionType.EPISODE_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        Pageable pageable = PageRequest.of(pageReqDto.getPage(), pageReqDto.getSize());

        Page<Recomment> recomments = recommentRepository.findActiveRecommentsByParentCommentId(
            commentId, pageable);

        return new PageResDto<>(
            recomments.getContent().stream()
                .map(RecommentResDto::of)
                .toList(),
            recomments.getTotalElements()
        );
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

        String recommendSetKey = RECOMMEND_SET_KEY_PREFIX + commentId;
        String recommendCountKey = RECOMMEND_COUNT_KEY_PREFIX + commentId;

        Long result = redisTemplate.execute(recommendScript,
            List.of(recommendSetKey, recommendCountKey), memberId.toString());
        if (result == null || result != 1) {
            throw new IllegalStateException("추천 처리 실패");
        }

        Recommendation recommendation = Recommendation.builder()
            .member(findMember)
            .comment(findComment)
            .build();
        recommendationRepository.save(recommendation);

        return CreateRecommendationResDto.builder()
            .id(recommendation.getId())
            .recommendationCount(getRecommendationCount(recommendCountKey))
            .build();
    }

    /**
     * 댓글 추천 해제
     */
    @Transactional
    public void unrecommendComment(Long memberId, Long commentId) {
        String recommendSetKey = RECOMMEND_SET_KEY_PREFIX + commentId;
        String recommendCountKey = RECOMMEND_COUNT_KEY_PREFIX + commentId;

        Long result = redisTemplate.execute(recommendScript,
            List.of(recommendSetKey, recommendCountKey), memberId.toString());
        if (result == null || result != -1) {
            throw new IllegalStateException("추천 해제 실패");
        }

        recommendationRepository.deleteByMemberAndComment(memberId, commentId);
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

        String notRecommendSetKey = NOT_RECOMMEND_SET_KEY_PREFIX + commentId;
        String notRecommendCountKey = NOT_RECOMMEND_COUNT_KEY_PREFIX + commentId;

        Long result = redisTemplate.execute(notRecommendScript,
            List.of(notRecommendSetKey, notRecommendCountKey), memberId.toString());
        if (result == null || result != 1) {
            throw new IllegalStateException("비추천 처리 실패");
        }

        NotRecommendation notRecommendation = NotRecommendation.builder()
            .member(findMember)
            .comment(findComment)
            .build();
        notRecommendationRepository.save(notRecommendation);

        return NotRecommendationResDto.builder()
            .id(notRecommendation.getId())
            .notRecommendationCount(getRecommendationCount(notRecommendCountKey))
            .build();
    }


    /**
     * 댓글 비추천 해제
     */
    @Transactional
    public void unnotRecommendComment(Long memberId, Long commentId) {
        String notRecommendSetKey = NOT_RECOMMEND_SET_KEY_PREFIX + commentId;
        String notRecommendCountKey = NOT_RECOMMEND_COUNT_KEY_PREFIX + commentId;

        Long result = redisTemplate.execute(notRecommendScript,
            List.of(notRecommendSetKey, notRecommendCountKey), memberId.toString());
        if (result == null || result != -1) {
            throw new IllegalStateException("비추천 해제 실패");
        }

        notRecommendationRepository.deleteByMemberAndComment(memberId, commentId);
    }

    /**
     * 댓글 추천 수, 비추천 수 가져오기
     */
    public int getRecommendationCount(String countKey) {
        String countValue = redisTemplate.opsForValue().get(countKey);
        return countValue == null ? 0 : Integer.parseInt(countValue);
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

        String recommendRecommentSetKey = RECOMMENT_RECOMMEND_SET_KEY_PREFIX + recommentId;
        String recommendRecommentCountKey = RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX + recommentId;

        Long result = redisTemplate.execute(recommendScript,
            List.of(recommendRecommentSetKey, recommendRecommentCountKey, memberId.toString()));
        if (result == null || result != 1) {
            throw new IllegalStateException("답글 추천 실패");
        }

        RecommentRecommendation newRecommentRecommendation = RecommentRecommendation
            .builder()
            .member(findMember)
            .recomment(findRecomment)
            .build();
        recommentRecommendationRepository.save(newRecommentRecommendation);

        return CreateRecommentRecommendationResDto.builder()
            .id(newRecommentRecommendation.getId())
            .recommentRecommendationCount(
                getRecommentRecommendationCount(recommendRecommentCountKey))
            .build();
    }

    /**
     * 답글 추천 해제
     */
    @Transactional
    public void unrecommentRecommendation(Long memberId, Long recommentId) {
        String recommendRecommentSetKey = RECOMMENT_RECOMMEND_SET_KEY_PREFIX + recommentId;
        String recommendRecommentCountKey = RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX + recommentId;

        Long result = redisTemplate.execute(recommendScript,
            List.of(recommendRecommentSetKey, recommendRecommentCountKey), memberId.toString());
        if (result == null || result != -1) {
            throw new IllegalStateException("추천 해제 실패");
        }

        recommentRecommendationRepository.deleteByMemberAndRecomment(memberId, recommentId);
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

        String notRecommendRecommentKey = RECOMMENT_NOT_RECOMMEND_SET_KEY_PREFIX + recommentId;
        String notRecommendRecommentCountKey =
            RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX + recommentId;

        Long result = redisTemplate.execute(notRecommendScript,
            List.of(notRecommendRecommentKey, notRecommendRecommentCountKey), memberId.toString());
        if (result == null || result != 1) {
            throw new IllegalStateException("비추천 처리 실패");
        }

        RecommentNotRecommendation newRecommentNotRecommendation = RecommentNotRecommendation.builder()
            .member(findMember)
            .recomment(findRecomment)
            .build();
        recommentNotRecommendationRepository.save(newRecommentNotRecommendation);

        return CreateRecommentNotRecommendationResDto.builder()
            .id(newRecommentNotRecommendation.getId())
            .notRecommendationCount(getRecommentRecommendationCount(notRecommendRecommentCountKey))
            .build();
    }

    /**
     * 답글 비추천 해제
     */
    @Transactional
    public void unrecommentNotRecommendation(Long memberId, Long recommentId) {
        String notRecommentRecommendSetKey = RECOMMENT_NOT_RECOMMEND_SET_KEY_PREFIX + recommentId;
        String notRecommentRecommendCountKey =
            RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX + recommentId;

        Long result = redisTemplate.execute(notRecommendScript,
            List.of(notRecommentRecommendSetKey, notRecommentRecommendCountKey),
            memberId.toString());
        if (result == null || result != -1) {
            throw new IllegalStateException("비추천 해제 실패");
        }

        recommentNotRecommendationRepository.deleteByMemberAndRecomment(memberId, recommentId);
    }

    /**
     * 답글 추천수/비추천수 가져오기
     */
    public int getRecommentRecommendationCount(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    /**
     * Redis와 DB 댓글 동기화
     */
    public void syncRecommendationCountToDatabase() {
        List<Long> commentIds = commentRepository.findAllCommentIds();

        for (Long commentId : commentIds) {
            String recommendCountKey = RECOMMEND_COUNT_KEY_PREFIX + commentId;
            String notRecommendCountKey = NOT_RECOMMEND_COUNT_KEY_PREFIX + commentId;

            String recommendCountValue = redisTemplate.opsForValue().get(recommendCountKey);
            String notRecommendCountValue = redisTemplate.opsForValue().get(notRecommendCountKey);

            int recommendCount =
                recommendCountValue == null ? 0 : Integer.parseInt(recommendCountValue);
            int notRecommendCount =
                notRecommendCountValue == null ? 0 : Integer.parseInt(notRecommendCountValue);

            Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

            comment.setRecommendationCount(recommendCount);
            comment.setNotRecommendationCount(notRecommendCount);
            commentRepository.save(comment);
        }
    }

    /**
     * Redis와 DB 답글 동기화
     */
    public void syncRecommentRecommendationCountToDatabase() {
        List<Long> recommentIds = recommentRepository.findAllRecommentIds();

        for (Long recommentId : recommentIds) {
            String recommendRecommentCountKey = RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX + recommentId;
            String notRecommendRecommentCountKey =
                RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX + recommentId;

            String recommendCountValue = redisTemplate.opsForValue()
                .get(recommendRecommentCountKey);
            String notRecommendCountValue = redisTemplate.opsForValue()
                .get(notRecommendRecommentCountKey);

            int recommendCount =
                recommendCountValue == null ? 0 : Integer.parseInt(recommendCountValue);
            int notRecommendCount =
                notRecommendCountValue == null ? 0 : Integer.parseInt(notRecommendCountValue);

            Recomment recomment = recommentRepository.findById(recommentId)
                .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

            recomment.setRecommendationCount(recommendCount);
            recomment.setNotRecommendationCount(notRecommendCount);
            recommentRepository.save(recomment);
        }
    }

    /**
     * Redis 댓글 추천/비추천 데이터 복구
     */
    @Transactional(readOnly = true)
    public void reloadCommentRedisFromDB() {
        List<Comment> comments = commentRepository.findAll();

        for (Comment comment : comments) {
            String recommendCountKey = RECOMMEND_COUNT_KEY_PREFIX + comment.getId();
            String notRecommendCountKey = NOT_RECOMMEND_COUNT_KEY_PREFIX + comment.getId();

            if (Boolean.FALSE.equals(redisTemplate.hasKey(recommendCountKey))) {
                redisTemplate.opsForValue()
                    .set(recommendCountKey, String.valueOf(comment.getRecommendationCount()));
            }

            if (Boolean.FALSE.equals(redisTemplate.hasKey(notRecommendCountKey))) {
                redisTemplate.opsForValue()
                    .set(notRecommendCountKey, String.valueOf(comment.getNotRecommendationCount()));
            }
        }
    }

    /**
     * Redis 답글 추천/비추천 데이터 복구
     */
    @Transactional(readOnly = true)
    public void reloadRecommentRedisFromDB() {
        List<Recomment> recomments = recommentRepository.findAll();

        for (Recomment recomment : recomments) {
            String recommendRecommentCountKey =
                RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX + recomment.getId();
            String notRecommendRecommentCountKey =
                RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX + recomment.getId();

            if (Boolean.FALSE.equals(redisTemplate.hasKey(recommendRecommentCountKey))) {
                redisTemplate.opsForValue()
                    .set(recommendRecommentCountKey,
                        String.valueOf(recomment.getRecommendationCount()));
            }

            if (Boolean.FALSE.equals(redisTemplate.hasKey(notRecommendRecommentCountKey))) {
                redisTemplate.opsForValue()
                    .set(notRecommendRecommentCountKey,
                        String.valueOf(recomment.getNotRecommendationCount()));
            }
        }
    }
}
