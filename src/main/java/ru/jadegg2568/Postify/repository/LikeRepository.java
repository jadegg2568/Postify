package ru.jadegg2568.Postify.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.entity.Like;
import ru.jadegg2568.Postify.entity.LikeId;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    long countByPost_Uuid(UUID postUuid);

    @EntityGraph(attributePaths = "user")
    List<Like> findByPost_UuidOrderByCreatedAtAsc(UUID postUuid);
}
