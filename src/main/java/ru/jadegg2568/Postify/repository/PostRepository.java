package ru.jadegg2568.Postify.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.entity.Post;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"author", "replyTo"})
    Optional<Post> findByUuid(UUID uuid);

    @EntityGraph(attributePaths = {"author"})
    Page<Post> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    Page<Post> findByTitleContainingIgnoreCase(String title, @NonNull Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.id = :postId")
    void incrementViews(@Param("postId") Long postId);
}
