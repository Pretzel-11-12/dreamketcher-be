package pretzel.dreamketcherbe.domain.comment.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
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
import pretzel.dreamketcherbe.domain.comment.dto.MyCommentsAndRecommentsListResDto;
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
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRecommendationRepository;
import pretzel.dreamketcherbe.domain.comment.repository.RecommentRepository;
import pretzel.dreamketcherbe.domain.episode.entity.Episode;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeException;
import pretzel.dreamketcherbe.domain.episode.exception.EpisodeExceptionType;
import pretzel.dreamketcherbe.domain.episode.repository.EpisodeRepository;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.exception.MemberException;
import pretzel.dreamketcherbe.domain.member.exception.MemberExceptionType;
import pretzel.dreamketcherbe.domain.member.repository.MemberRepository;
import pretzel.dreamketcherbe.domain.report.entity.CommentReport;
import pretzel.dreamketcherbe.domain.report.entity.ReportReason;
import pretzel.dreamketcherbe.domain.report.repository.CommentReportRepository;
import pretzel.dreamketcherbe.domain.report.repository.ReportReasonRepository;

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
    private final RecommentRecommendationRepository recommentRecommendationRepository;
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

    private static final String ADD_LUA_SCRIPT = """
        local isMember = redis.call('sismember', KEYS[1], ARGV[1])
        if isMember == 0 then
            redis.call('sadd', KEYS[1], ARGV[1])
            redis.call('incr', KEYS[2])
            return 1
        else
            return 0
        end
        """;
    private final RedisScript<Long> addRecommendScript = new DefaultRedisScript<>(
        ADD_LUA_SCRIPT,
        Long.class);

    private final RedisScript<Long> recommend = addRecommendScript;
    private final RedisScript<Long> notRecommend = addRecommendScript;

    private static final String REMOVE_LUA_SCRIPT = """
        local isMember = redis.call('sismember', KEYS[1], ARGV[1])
            if isMember == 1 then
                redis.call('srem', KEYS[1], ARGV[1])
        
                local currentCount = tonumber(redis.call('get', KEYS[2]) or "0")
                if currentCount > 0 then
                    redis.call('decr', KEYS[2])
                end
                return -1
            else
                return 0
            end
        """;
    private final RedisScript<Long> removeRecommendScript = new DefaultRedisScript<>(
        REMOVE_LUA_SCRIPT,
        Long.class);

    private final RedisScript<Long> removeRecommend = removeRecommendScript;
    private final RedisScript<Long> removeNotRecommend = removeRecommendScript;
    private final ReportReasonRepository reportReasonRepository;
    private final CommentReportRepository commentReportRepository;


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
     * 댓글 논리 삭제
     */
    @Transactional
    public void deleteComment(Long memberId, Long episodeId, Long commentId) {
        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        findComment.isAuthor(memberId);
        List<Long> recommentIds = recommentRepository.findByComment(commentId);

        if (recommentIds.isEmpty()) {
            recommendationRepository.deleteBycommentId(commentId);
            notRecommendationRepository.deleteBycommentId(commentId);
            redisTemplate.delete(RECOMMEND_SET_KEY_PREFIX + commentId);
            redisTemplate.delete(RECOMMEND_COUNT_KEY_PREFIX + commentId);
            redisTemplate.delete(NOT_RECOMMEND_SET_KEY_PREFIX + commentId);
            redisTemplate.delete(NOT_RECOMMEND_COUNT_KEY_PREFIX + commentId);
            findComment.softDelete();
            commentRepository.save(findComment);
            return;
        }

        recommendationRepository.deleteBycommentId(commentId);
        notRecommendationRepository.deleteBycommentId(commentId);
        redisTemplate.delete(RECOMMEND_SET_KEY_PREFIX + commentId);
        redisTemplate.delete(RECOMMEND_COUNT_KEY_PREFIX + commentId);
        redisTemplate.delete(NOT_RECOMMEND_SET_KEY_PREFIX + commentId);
        redisTemplate.delete(NOT_RECOMMEND_COUNT_KEY_PREFIX + commentId);
        findComment.softDelete();
        commentRepository.save(findComment);

        recommentRecommendationRepository.deleteByRecommentId(recommentIds);
        recommentNotRecommendationRepository.deleteByRecomment(recommentIds);

        List<String> redisDeleteKeys = new ArrayList<>();
        for (Long recommentId : recommentIds) {
            redisDeleteKeys.add(RECOMMENT_RECOMMEND_SET_KEY_PREFIX + recommentId);
            redisDeleteKeys.add(RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX + recommentId);
            redisDeleteKeys.add(RECOMMENT_NOT_RECOMMEND_SET_KEY_PREFIX + recommentId);
            redisDeleteKeys.add(RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX + recommentId);
        }
        redisTemplate.delete(redisDeleteKeys);
        recommentRepository.deleteByComment(commentId);
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
     * 답글 논리 삭제
     */
    @Transactional
    public void deleteRecomment(Long memberId, Long episodeId, Long commentId, Long recommentId) {
        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        findRecomment.isAuthor(memberId);

        recommentRecommendationRepository.deleteByRecomment(recommentId);
        recommentNotRecommendationRepository.deleteByRecomment(recommentId);
        redisTemplate.delete(RECOMMENT_RECOMMEND_SET_KEY_PREFIX + recommentId);
        redisTemplate.delete(RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX + recommentId);
        redisTemplate.delete(RECOMMENT_NOT_RECOMMEND_SET_KEY_PREFIX + recommentId);
        redisTemplate.delete(RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX + recommentId);
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
     * 내 댓글, 답글 조회
     */
    @Transactional(readOnly = true)
    public PageResDto<MyCommentsAndRecommentsListResDto> getMyCommentsAndRecomments(Long memberId,
        String type,
        PageReqDto pageReqDto) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        List<MyCommentsAndRecommentsListResDto> myCommentsAndRecommentsListResDtoList = new ArrayList<>();

        Sort sort = Sort.by(Direction.DESC, "createdAt");

        if ("comment".equals(type) || type == null) {
            List<MyCommentsAndRecommentsListResDto> comments = commentRepository.findByMemberIdAndDeletedFalse(
                    memberId, sort)
                .stream()
                .map(MyCommentsAndRecommentsListResDto::from)
                .toList();
            myCommentsAndRecommentsListResDtoList.addAll(comments);
        }
        if ("recomment".equals(type) || type == null) {
            List<MyCommentsAndRecommentsListResDto> recomments = recommentRepository.findByMemberIdAndIsDeletedFalse(
                    memberId, sort)
                .stream()
                .map(MyCommentsAndRecommentsListResDto::from)
                .toList();
            myCommentsAndRecommentsListResDtoList.addAll(recomments);
        }

        myCommentsAndRecommentsListResDtoList.sort(
            Comparator.comparing(MyCommentsAndRecommentsListResDto::createdAt).reversed());

        long totalElements = myCommentsAndRecommentsListResDtoList.size();

        int start = pageReqDto.getPage() * pageReqDto.getSize();
        int end = Math.min(start + pageReqDto.getSize(),
            myCommentsAndRecommentsListResDtoList.size());

        List<MyCommentsAndRecommentsListResDto> pagingList = myCommentsAndRecommentsListResDtoList.subList(
            start, end);

        return new PageResDto<>(pagingList, totalElements);
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

        Long result = redisTemplate.execute(recommend,
            List.of(recommendSetKey, recommendCountKey), String.valueOf(memberId));

        if (result == null || result != 1) {
            throw new IllegalStateException("추천 처리 실패");
        }

        Recommendation recommendation = Recommendation.addOf(findComment, findMember);
        recommendationRepository.save(recommendation);

        findComment.updateRecommendationCount(getRecommendationCount(recommendCountKey));

        return CreateRecommendationResDto.of(recommendation,
            getRecommendationCount(recommendCountKey));
    }

    /**
     * 댓굴 추천 해제
     */
    @Transactional
    public int unrecommendComment(Long memberId, Long commentId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        String recommendSetKey = RECOMMEND_SET_KEY_PREFIX + commentId;
        String recommendCountKey = RECOMMEND_COUNT_KEY_PREFIX + commentId;

        Long result = redisTemplate.execute(removeRecommend,
            List.of(recommendSetKey, recommendCountKey), String.valueOf(memberId));

        if (result == null || result != -1) {
            throw new IllegalStateException("추천 해제 실패");
        }

        recommendationRepository.deleteByMemberAndComment(memberId, commentId);
        findComment.updateRecommendationCount(getRecommendationCount(recommendCountKey));

        return getRecommendationCount(recommendCountKey);
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

        Long result = redisTemplate.execute(notRecommend,
            List.of(notRecommendSetKey, notRecommendCountKey), String.valueOf(memberId));
        if (result == null || result != 1) {
            throw new IllegalStateException("비추천 처리 실패");
        }

        NotRecommendation notRecommendation = NotRecommendation.addOf(findComment, findMember);
        notRecommendationRepository.save(notRecommendation);

        findComment.updateNotRecommendationCount(getRecommendationCount(notRecommendCountKey));

        return NotRecommendationResDto.of(notRecommendation,
            getRecommendationCount(notRecommendCountKey));
    }


    /**
     * 댓글 비추천 해제
     */
    @Transactional
    public int unnotRecommendComment(Long memberId, Long commentId) {
        Member findMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));

        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        String notRecommendSetKey = NOT_RECOMMEND_SET_KEY_PREFIX + commentId;
        String notRecommendCountKey = NOT_RECOMMEND_COUNT_KEY_PREFIX + commentId;

        Long result = redisTemplate.execute(removeNotRecommend,
            List.of(notRecommendSetKey, notRecommendCountKey), String.valueOf(memberId));

        if (result == null || result != -1) {
            throw new IllegalStateException("비추천 해제 실패");
        }

        notRecommendationRepository.deleteByMemberAndComment(memberId, commentId);
        findComment.updateNotRecommendationCount(getRecommendationCount(notRecommendCountKey));

        return getRecommendationCount(notRecommendCountKey);
    }

    /**
     * 추천 수, 비추천 수 가져오기
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

        Long result = redisTemplate.execute(recommend,
            List.of(recommendRecommentSetKey, recommendRecommentCountKey),
            String.valueOf(memberId));

        if (result == null || result != 1) {
            throw new IllegalStateException("답글 추천 실패");
        }

        RecommentRecommendation newRecommentRecommendation = RecommentRecommendation.addOf(
            findMember, findRecomment);
        recommentRecommendationRepository.save(newRecommentRecommendation);

        findRecomment.updateRecommendationCount(
            getRecommentRecommendationCount(recommendRecommentCountKey));

        return CreateRecommentRecommendationResDto.of(newRecommentRecommendation,
            getRecommentRecommendationCount(recommendRecommentCountKey));
    }

    /**
     * 답글 추천 해제
     */
    @Transactional
    public int unrecommentRecommendation(Long memberId, Long recommentId) {
        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        String recommendRecommentSetKey = RECOMMENT_RECOMMEND_SET_KEY_PREFIX + recommentId;
        String recommendRecommentCountKey = RECOMMENT_RECOMMEND_COUNT_KEY_PREFIX + recommentId;

        Long result = redisTemplate.execute(removeRecommend,
            List.of(recommendRecommentSetKey, recommendRecommentCountKey),
            String.valueOf(memberId));

        if (result == null || result != -1) {
            throw new IllegalStateException("추천 해제 실패");
        }

        recommentRecommendationRepository.deleteByMemberAndRecomment(memberId, recommentId);
        findRecomment.updateRecommendationCount(
            getRecommentRecommendationCount(recommendRecommentCountKey));

        return getRecommentRecommendationCount(recommendRecommentCountKey);
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

        Long result = redisTemplate.execute(notRecommend,
            List.of(notRecommendRecommentKey, notRecommendRecommentCountKey),
            String.valueOf(memberId));

        if (result == null || result != 1) {
            throw new IllegalStateException("비추천 처리 실패");
        }

        RecommentNotRecommendation newRecommentNotRecommendation = RecommentNotRecommendation.addOf(
            findMember, findRecomment);
        recommentNotRecommendationRepository.save(newRecommentNotRecommendation);

        findRecomment.updateNotRecommendationCount(
            getRecommentRecommendationCount(notRecommendRecommentCountKey));

        return CreateRecommentNotRecommendationResDto.of(newRecommentNotRecommendation,
            getRecommentRecommendationCount(notRecommendRecommentCountKey));
    }

    /**
     * 답글 비추천 해제
     */
    @Transactional
    public int unrecommentNotRecommendation(Long memberId, Long recommentId) {
        Recomment findRecomment = recommentRepository.findById(recommentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));

        String notRecommentRecommendSetKey = RECOMMENT_NOT_RECOMMEND_SET_KEY_PREFIX + recommentId;
        String notRecommentRecommendCountKey =
            RECOMMENT_NOT_RECOMMEND_COUNT_KEY_PREFIX + recommentId;

        Long result = redisTemplate.execute(removeNotRecommend,
            List.of(notRecommentRecommendSetKey, notRecommentRecommendCountKey),
            String.valueOf(memberId));

        if (result == null || result != -1) {
            throw new IllegalStateException("비추천 해제 실패");
        }

        recommentNotRecommendationRepository.deleteByMemberAndRecomment(memberId, recommentId);
        findRecomment.updateNotRecommendationCount(
            getRecommentRecommendationCount(notRecommentRecommendCountKey));

        return getRecommentRecommendationCount(notRecommentRecommendCountKey);
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

            comment.updateRecommendationCount(recommendCount);
            comment.updateNotRecommendationCount(notRecommendCount);
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

            recomment.updateRecommendationCount(recommendCount);
            recomment.updateNotRecommendationCount(notRecommendCount);
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
     * 데이터베이스의 답글 추천 및 비추천 수를 Redis에 복구합니다.
     *
     * Redis에 해당 답글의 추천 또는 비추천 카운트 키가 없을 경우, 데이터베이스 값을 기반으로 Redis에 값을 설정합니다.
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

    /**
     * 댓글을 신고합니다.
     *
     * 지정한 회원, 댓글, 신고 사유 및 추가 사유 텍스트를 기반으로 댓글 신고를 생성하고 저장합니다.
     * 신고된 댓글은 신고 상태로 변경됩니다.
     *
     * @param memberId 신고를 수행하는 회원의 ID
     * @param commentId 신고 대상 댓글의 ID
     * @param reasonId 신고 사유의 ID
     * @param reasonText 추가 신고 사유(선택 사항)
     *
     * @throws CommentException 댓글이 존재하지 않을 경우 발생합니다.
     * @throws IllegalStateException 신고 사유가 존재하지 않을 경우 발생합니다.
     */
    @Transactional
    public void reportComment(Long memberId, Long commentId, Long reasonId, String reasonText) {
        Comment findComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));

        ReportReason findReason = reportReasonRepository.findById(reasonId)
            .orElseThrow(() -> new IllegalStateException()); // 추후 수정

        CommentReport findCommentReport = CommentReport.forMember(commentId, memberId, findReason,
            reasonText);
        commentReportRepository.save(findCommentReport);

        findComment.report();
        commentRepository.save(findComment);
    }

//    /**
//     * 답글 신고 요청
//     */
//    @Transactional
//    public void reportRecomment(Long memberId, Long commentId, Long recommentId, Long reasonId,
//        String reasonText) {
//        Comment findComment = commentRepository.findById(commentId)
//            .orElseThrow(() -> new CommentException(CommentExceptionType.COMMENT_NOT_FOUND));
//
//        Recomment findRecomment = recommentRepository.findById(recommentId)
//            .orElseThrow(() -> new CommentException(CommentExceptionType.RECOMMENT_NOT_FOUND));
//
//        ReportReason findReason = reportReasonRepository.findById(reasonId)
//            .orElseThrow(() -> new IllegalStateException()); // 추후 수정
//
//        CommentReport findCommentReport = CommentReport.forMember(memberId, commentId, recommentId,
//            findReason,
//            reasonText);
//        commentReportRepository.save(findCommentReport);
//
//        findRecomment.report();
//        recommentRepository.save(findRecomment);
//    }

}
