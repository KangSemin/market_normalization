package no.gunbang.market.domain.user.service;

import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.user.dto.LoginRequestDto;
import no.gunbang.market.domain.user.dto.UserResponseDto;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long login(LoginRequestDto dto){
        Long userId = userRepository.findIdByEmail(dto.getEmail());
        if(userId == null || !passwordEncoder.matches(dto.getPassword(), userRepository.findPasswordById(userId))){
            throw new CustomException(ErrorCode.WRONG_EMAIL_OR_PASSWORD);
        }
        return userId;
    }

    public UserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.toDto(user);
    }
}
