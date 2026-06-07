package ru.jadegg2568.Postify.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.entity.Dialogue;
import ru.jadegg2568.Postify.entity.Message;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByUuid(UUID uuid);

    Page<Message> findByDialogueOrderByCreatedAtAsc(Dialogue dialogue, Pageable pageable);
}