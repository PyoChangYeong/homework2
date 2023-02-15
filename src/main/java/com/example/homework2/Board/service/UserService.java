package com.example.homework2.Board.service;


import com.example.homework2.Board.dto.MegResponseDto;
import com.example.homework2.Board.dto.UserRequestDto;
import com.example.homework2.Board.entity.User;
import com.example.homework2.Board.jwt.JwtUtil;
import com.example.homework2.Board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
@Transactional
    public ResponseEntity<MegResponseDto> signup(UserRequestDto userRequestDto, BindingResult result) {
        String username = userRequestDto.getUsername();
        String password = userRequestDto.getPassword();

        // 입력한 username, password 유효성 검사 통과 못한 경우
        if (result.hasErrors()) {
            return ResponseEntity.badRequest()  // status : bad request
                    .body(MegResponseDto.builder()  // body : SuccessResponseDto (statusCode, msg)
                            .Code(HttpStatus.BAD_REQUEST.value())
                            .msg(result.getAllErrors().get(0).getDefaultMessage())
                            .build());
        }

        // 회원 중복 확인
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            return ResponseEntity.badRequest()  // status : bad request
                    .body(MegResponseDto.builder()  // body : SuccessResponseDto (statusCode, msg)
                            .Code(HttpStatus.BAD_REQUEST.value())
                            .msg("중복된 사용자가 존재합니다.")
                            .build());
        }

        // 입력한 username, password 로 user 객체 만들어 repository 에 저장
        userRepository.save(User.builder()
                .username(username)
                .password(password)
                .build());

        return ResponseEntity.ok(MegResponseDto.builder()   // status : ok
                .Code(HttpStatus.OK.value())  // body : SuccessResponseDto (statusCode, msg)
                .msg("회원가입 성공")
                .build());

    }

@Transactional
    public ResponseEntity<MegResponseDto> login(UserRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

//                              사용자확인
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            return ResponseEntity.badRequest()          //  status : badRequest
                    .body(MegResponseDto.builder()      //  body    :   MegResponseDto -> Code, msg
                            .Code(HttpStatus.BAD_REQUEST.value())
                            .msg("등록되지 않았습니다.")
                            .build());
        }

        if(!user.get().getPassword().equals(password)){
            return ResponseEntity.badRequest()
                    .body(MegResponseDto.builder()
                            .Code(HttpStatus.BAD_REQUEST.value())
                            .msg("비밀번호가 틀렸습니다.")
                            .build());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.get().getUsername()));
        return ResponseEntity.ok()      //  status -> ok
                .headers(headers)       //  headers -> JWT
                .body(MegResponseDto.builder()
                        .Code(HttpStatus.OK.value())
                        .msg("로그인이 되었습니다")
                        .build());

    }
}
