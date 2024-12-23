package pretzel.dreamketcherbe.domain.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentReqDto;
import pretzel.dreamketcherbe.domain.comment.dto.CreateCommentResDto;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;
import pretzel.dreamketcherbe.domain.comment.exception.CommentException;
import pretzel.dreamketcherbe.domain.comment.exception.CommentExceptionType;
import pretzel.dreamketcherbe.domain.comment.repository.CommentRepository;
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

}
