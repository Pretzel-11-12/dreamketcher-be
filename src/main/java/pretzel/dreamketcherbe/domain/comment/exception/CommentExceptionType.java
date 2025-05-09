package pretzel.dreamketcherbe.domain.comment.exception;

import org.springframework.http.HttpStatus;
import pretzel.dreamketcherbe.common.exception.ExceptionType;

public enum CommentExceptionType implements ExceptionType {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    RECOMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "답글을 찾을 수 없습니다."),
    UNAUTHORIZED_MEMBER(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다."),
    NOT_RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 비추천을 찾을 수 없습니다."),
    RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 추천을 찾을 수 없습니다."),
    RECOMMENT_RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "답글 추천을 찾을 수 없습니다."),
    RECOMMENT_NOT_RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "답글 비추천을 찾을 수 없습니다."),
    COMMENT_RECOMMEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 추천에 실패하였습니다."),
    COMMENT_UNRECOMMEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 추천 해제에 실패하였습니다."),
    COMMENT_NOT_RECOMMEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 비추천에 실패하였습니다."),
    COMMENT_UN_NOT_RECOMMEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 비추천 해제에 실패하였습니다."),
    RECOMMENT_RECOMMEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "답글 추천에 실패하였습니다."),
    RECOMMENT_UNRECOMMEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "답글 추천 해제에 실패하였습니다."),
    RECOMMENT_NOT_RECOMMEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "답글 비추천에 실패하였습니다."),
    RECOMMENT_UNNOT_RECOMMEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "답글 비추천 해제에 실패하였습니다."),
    REPORTED_COMMENT(HttpStatus.BAD_REQUEST, "이미 신고된 댓글입니다."),
    REPORTED_RECOMMENT(HttpStatus.BAD_REQUEST, "이미 신고된 대댓글입니다.");


    private final HttpStatus status;
    private final String message;

    CommentExceptionType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public String code() {
        return this.name();
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}
