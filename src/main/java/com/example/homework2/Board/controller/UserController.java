package com.example.homework2.Board.controller;


import com.example.homework2.Board.dto.MegResponseDto;
import com.example.homework2.Board.dto.UserRequestDto;
import com.example.homework2.Board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Board")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<MegResponseDto> signup(@Valid @RequestBody UserRequestDto userRequestDto, BindingResult result){
        return userService.signup(userRequestDto,result);
    }

    @PostMapping("/login")
    public ResponseEntity<MegResponseDto> login(@RequestBody UserRequestDto requestDto){
        return userService.login(requestDto);
    }


}
