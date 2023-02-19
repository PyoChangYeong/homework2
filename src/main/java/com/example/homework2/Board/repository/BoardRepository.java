package com.example.homework2.Board.repository;

import com.example.homework2.Board.entity.Board;
import com.example.homework2.Board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByOrderByModifiedAtDesc();
    Optional<Board> findByIdAndUser(Long id, User user);
    Optional<Board> findByIdAndUserId(Long id,Long userid);
}
