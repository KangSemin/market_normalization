package no.gunbang.market.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.domain.user.entity.User;

@Builder
@Getter
@AllArgsConstructor
public class UserResponseDto {

    private String nickname;
    private String server;
    private short level;
    private String job;
    private long gold;

    public static UserResponseDto toDto(User user){
        return UserResponseDto.builder()
            .nickname(user.getNickname())
            .server(user.getServer())
            .level(user.getLevel())
            .job(user.getJob())
            .gold(user.getGold())
            .build();
    }
}
