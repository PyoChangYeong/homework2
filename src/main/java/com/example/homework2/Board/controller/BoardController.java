package com.example.homework2.Board.controller;


import com.example.homework2.Board.dto.BoardRequestDto;
import com.example.homework2.Board.dto.BoardResponseDto;
import com.example.homework2.Board.dto.MegResponseDto;
import com.example.homework2.Board.entity.Board;
import com.example.homework2.Board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

//                      게시글 전체 목록 조회
    @GetMapping("/api/Board")
    public List<BoardResponseDto> getBoard(){
        return boardService.getBoard();
    }

//                      게시글 작성
    @PostMapping("/api/Board")
    public BoardResponseDto createPost(@RequestBody BoardRequestDto requestsDto, HttpServletRequest request) {
        return boardService.createPost(requestsDto, request);
    }

//                      선택된 게시글 조회
    @GetMapping("/api/Board/{id}")
    public BoardResponseDto getPost(@PathVariable Long id){
        return boardService.getPost(id);
    }

//                      선택된 게시글 수정
    @PutMapping("/api/Board/{id}")
    public ResponseEntity<MegResponseDto> updatePost(@PathVariable Long id, @RequestBody BoardRequestDto requestDto, HttpServletRequest request){
        return boardService.updatePost(id, requestDto, request);
    }


//                      선택된 게시글 삭제
    @DeleteMapping("/api/Board/{id}")
    public ResponseEntity<MegResponseDto> deletePost(@PathVariable Long id, HttpServletRequest request){
        return boardService.deletePost(id, request);
    }



}
