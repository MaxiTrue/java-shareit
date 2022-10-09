package ru.practicum.shareit.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.model.Comment;

import java.util.Set;

public interface CommentStorage extends JpaRepository<Comment, Long> {

    Set<Comment> findAllByItemIdOrderByCreatedDesc(long itemId);

}
