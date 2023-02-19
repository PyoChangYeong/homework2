package com.example.homework2.Board.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@RequiredArgsConstructor
public class UserRequestDto {

//    아이디 유효성 검사
    @Pattern(regexp = "^[a-z0-9]{3,9}$", message = "아이디는 4~10자리 영문 소문자,숫자를 포함시켜주세요")
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9]{7,14}$",message = "비밀번호는 8~15자리 영문 대소문자,숫자를 포함시켜주세요")
    private String password;

    private boolean admin = false;

    private String adminToken = "";

}
