package ru.jadegg2568.Postify.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, PostViewId> {

    int deleteByCreatedAtBefore(Instant cutoff);
}
