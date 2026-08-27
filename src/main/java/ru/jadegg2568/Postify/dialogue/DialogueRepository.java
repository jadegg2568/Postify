package ru.jadegg2568.Postify.dialogue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.user.User;

import java.util.Optional;

@Repository
public interface DialogueRepository extends JpaRepository<Dialogue, Long> {

    Optional<Dialogue> findByUuid(java.util.UUID uuid);

    // Find dialogue by the exact ordered pair (user1,user2)
    Optional<Dialogue> findByUser1AndUser2(User user1, User user2);

    Page<Dialogue> findByUser1OrUser2(User user, User user1, Pageable pageable);
}