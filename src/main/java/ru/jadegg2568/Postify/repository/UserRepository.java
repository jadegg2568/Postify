package ru.jadegg2568.Postify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jadegg2568.Postify.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(UUID uuid);

    @Query("SELECT u FROM User u WHERE u.mail = :query OR u.name = :query")
    Optional<User> findByLogin(@Param("query") String query);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:query% OR u.displayName LIKE %:query%")
    List<User> searchByQuery(@Param("query") String query);

    boolean existsByMail(@Param("mail") String mail);
    boolean existsByName(@Param("name") String name);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.mail = :mail OR u.name = :name")
    boolean existsByMailOrName(@Param("mail") String mail, @Param("name") String name);

    boolean existsByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
