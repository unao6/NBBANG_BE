package com.elice.nbbang.domain.chat.repository;

import com.elice.nbbang.domain.chat.entity.ArchivedChats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivedChatRepository extends JpaRepository<ArchivedChats, Long> {
    Page<ArchivedChats> findAll(Pageable pageable);
    boolean existsByChatId(Long chatId);
}
