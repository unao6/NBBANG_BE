package com.elice.nbbang.domain.chat.repository;

import com.elice.nbbang.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByUserIdAndStatus(Long userId, Boolean status);

    Optional<Chat> findByUserId(Long userId);

    Page<Chat> findAll(Pageable pageable);

    Page<Chat> findAllByUserId(Long userId, Pageable pageable);

    List<Chat> findByLastRepliedAtAndStatus(LocalDateTime dateTime, Boolean status);

    void deleteByEndedAtAndStatus(LocalDateTime dateTime, Boolean status);

    @Query(value = "SELECT * FROM chat WHERE JSON_LENGTH(messages) = 0 " +
            "OR (JSON_LENGTH(messages) = 1 AND JSON_UNQUOTE(JSON_EXTRACT(messages, '$[0].nickname')) = 'System')",
            nativeQuery = true)
    List<Chat> findEmptyChats();
}
