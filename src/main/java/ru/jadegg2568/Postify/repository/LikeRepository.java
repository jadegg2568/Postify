package ru.jadegg2568.Postify.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.entity.Like;
import ru.jadegg2568.Postify.entity.LikeId;
import ru.jadegg2568.Postify.entity.Post;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    long countByPost(Post post);

    @EntityGraph(attributePaths = "user")
    List<Like> findByPostOrderByCreatedAtAsc(Post post);
}
