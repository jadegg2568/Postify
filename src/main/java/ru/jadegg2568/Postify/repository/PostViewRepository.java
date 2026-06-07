package ru.jadegg2568.Postify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.entity.PostView;
import ru.jadegg2568.Postify.entity.PostViewId;

import java.time.Instant;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, PostViewId> {

    int deleteByCreatedAtBefore(Instant cutoff);
}
