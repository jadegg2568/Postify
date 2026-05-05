package ru.jadegg2568.Postify.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.entity.Post;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUuid(UUID uuid);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    Page<Post> findAll(@NonNull Pageable pageable);

}
