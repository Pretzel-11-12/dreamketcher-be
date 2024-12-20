package pretzel.dreamketcherbe.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
