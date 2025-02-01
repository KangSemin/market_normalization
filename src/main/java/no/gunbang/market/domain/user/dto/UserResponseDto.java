package no.gunbang.market.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import no.gunbang.market.domain.user.entity.User;

@Builder
@Getter
public class UserResponseDto {

    private String nickname;
    private String server;
    private short level;
    private String job;
    private long gold;

    @QueryProjection
    public UserResponseDto(
        String nickname,
        String server,
        short level,
        String job,
        long gold
    )
    {
        this.nickname = nickname;
        this.server = server;
        this.level = level;
        this.job = job;
        this.gold = gold;
    }

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
