package ru.jadegg2568.Postify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.entity.Session;
import ru.jadegg2568.Postify.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByUuid(UUID sessionUuid);

    @Query("SELECT s FROM Session s WHERE s.user.uuid = :userUuid AND s.uuid = :sessionUuid")
    Optional<Session> findByDetailedInfo(
                    @Param("userUuid") UUID userUuid,
                    @Param("sessionUuid") UUID sessionUuid
            );

    List<Session> findByUserId(Long userId);

    List<Session> deleteByUserId(Long userId);
    void deleteByUuid(UUID uuid);

}
