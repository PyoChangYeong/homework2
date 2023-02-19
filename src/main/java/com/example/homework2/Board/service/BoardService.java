package com.example.homework2.Board.service;


import com.example.homework2.Board.dto.BoardRequestDto;
import com.example.homework2.Board.dto.BoardResponseDto;
import com.example.homework2.Board.dto.MegResponseDto;
import com.example.homework2.Board.entity.Board;
import com.example.homework2.Board.entity.User;
import com.example.homework2.Board.entity.UserRoleEnum;
import com.example.homework2.Board.jwt.JwtUtil;
import com.example.homework2.Board.repository.BoardRepository;
import com.example.homework2.Board.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
@Getter
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    
    
    //          게시글 전체 목록 조회
    public List<BoardResponseDto> getBoard(){
        return boardRepository.findAllByOrderByModifiedAtDesc().stream().map(BoardResponseDto::new).toList();
    };


    //          게시글 전체 목록 조회
//    @Transactional(readOnly = true)
//    public List<BoardResponseDto> getBoard() {
//        List<Board> boards = getBoardRepository().findAllByOrderByModifiedAtDesc();
//        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
//        for (Board bo : boards) {
//            boardResponseDtos.add(new BoardResponseDto(bo));
//        }
//        return boardRepository.findAllByOrderByModifiedAtDesc().stream().map(BoardResponseDto::new).toList();
//                  toList() : ArrayList() 사용 시 스트림으로 변환할 때 사용

//    }

//    private List<Board> getBoards() {
//        List<Board> boards = boardRepository.findAll().stream().map(BoardResponseDto::new).toList();
//        return boards;
//    }


    //            게시글 작성
    @Transactional
    public BoardResponseDto createPost(BoardRequestDto requestDto, HttpServletRequest request) {

        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // token 이 없으면 게시글 작성 불가
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            // 게시글 저장 후 responseDto 로 담아서 반환
            return new BoardResponseDto(boardRepository.save(Board.builder()        //  builder()  : 생성자 만들고 builder를 붙이면 1개가 생략 가능
                            .boardRequestDto(requestDto)
                    .user(user)
                    .build()));
        }

        return null;
    }

    //      선택한 게시글 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getPost(Long id) {
        return new BoardResponseDto(getElseThrow(id));
    }


    //      아이디 예외 처리
    private Board getElseThrow(Long id) {
        return boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
    }



    @Transactional
    public ResponseEntity<MegResponseDto> updatePost(Long id, BoardRequestDto requestDto, HttpServletRequest request) {



        Optional<Board> board;
        String token = jwtUtil.resolveToken(request);
        Claims claims;



        // 토큰이 있는 경우에만 수정 가능
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                return ResponseEntity.badRequest()  // status : bad request
                        .body(MegResponseDto.builder()  // body : SuccessResponseDto (statusCode, msg)
                                .Code(HttpStatus.BAD_REQUEST.value())
                                .msg("토근 에러")
                                .build());
            }



            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            Optional <User> user = userRepository.findByUsername(claims.getSubject());
            if(user.isEmpty()){
                return ResponseEntity.badRequest()  // status : bad request
                        .body(MegResponseDto.builder()  // body : SuccessResponseDto (statusCode, msg)
                                .Code(HttpStatus.BAD_REQUEST.value())
                                .msg("유저 없음")
                                .build());
            }



//            관리자일 경우 전부 수정
            if(user.get().getRole() == UserRoleEnum.ADMIN){
               board = boardRepository.findById(id);
            }else{
                board = boardRepository.findByIdAndUserId(id, user.get().getId());
            }




            if(board.isEmpty()){
                return ResponseEntity.badRequest()  // status : bad request
                        .body(MegResponseDto.builder()  // body : SuccessResponseDto (statusCode, msg)
                                .Code(HttpStatus.BAD_REQUEST.value())
                                .msg("게시글 없음")
                                .build());
            }



//            if(board.isEmpty()){
//                throw new IllegalArgumentException("토할 거 같다");
//            }
            // 선택한 게시글의 id와 토큰에서 가져온 사용자 정보가 일치하는 게시물이 있는지 확인
//            board =  boardRepository.findByIdAndUser(id, user).orElseThrow(
//                    () -> new IllegalArgumentException("본인이 작성한 게시글만 수정이 가능합니다.")
//            );

            // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정
            board.get().update(requestDto, user.get());
            return ResponseEntity.ok(MegResponseDto.builder()   // status : ok
                    .Code(HttpStatus.OK.value())  // body : SuccessResponseDto (statusCode, msg)
                    .msg("게시글 수정 완료")
                    .build());
        }

        return null;
    }


    @Transactional
    public ResponseEntity<MegResponseDto> deletePost(Long id, HttpServletRequest request) {
        Optional<Board> board;
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        // 토큰이 있는 경우에만 수정 가능
        if (token != null) {
            // Token 검증
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                return ResponseEntity.badRequest()  // status : bad request
                        .body(MegResponseDto.builder()  // body : SuccessResponseDto (statusCode, msg)
                                .Code(HttpStatus.BAD_REQUEST.value())
                                .msg("토근 에러")
                                .build());
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            Optional <User> user = userRepository.findByUsername(claims.getSubject());
            if(user.isEmpty()){
                return ResponseEntity.badRequest()  // status : bad request
                        .body(MegResponseDto.builder()  // body : SuccessResponseDto (statusCode, msg)
                                .Code(HttpStatus.BAD_REQUEST.value())
                                .msg("유저 없음")
                                .build());
            }

//            관리자일 경우 전부 수정
            if(user.get().getRole() == UserRoleEnum.ADMIN){
                board = boardRepository.findById(id);
            }else{
                board = boardRepository.findByIdAndUserId(id, user.get().getId());
            }



            if(board.isEmpty()){
                return ResponseEntity.badRequest()  // status : bad request
                        .body(MegResponseDto.builder()  // body : SuccessResponseDto (statusCode, msg)
                                .Code(HttpStatus.BAD_REQUEST.value())
                                .msg("게시글 없음")
                                .build());
            }



//            if(board.isEmpty()){
//                throw new IllegalArgumentException("토할 거 같다");
//            }
            // 선택한 게시글의 id와 토큰에서 가져온 사용자 정보가 일치하는 게시물이 있는지 확인
//            board =  boardRepository.findByIdAndUser(id, user).orElseThrow(
//                    () -> new IllegalArgumentException("본인이 작성한 게시글만 수정이 가능합니다.")
//            );


            // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정
            boardRepository.deleteById(id);
            return ResponseEntity.ok(MegResponseDto.builder()   // status : ok
                    .Code(HttpStatus.OK.value())  // body : SuccessResponseDto (statusCode, msg)
                    .msg("삭제 완료")
                    .build());
        }

        return null;
    }

}