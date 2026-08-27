package ru.jadegg2568.Postify.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    long countByPost(Post post);

    @EntityGraph(attributePaths = "user")
    List<Like> findByPostOrderByCreatedAtAsc(Post post);
}
