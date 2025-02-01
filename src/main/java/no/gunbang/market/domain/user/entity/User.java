package no.gunbang.market.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "`user`")
@Getter
@NoArgsConstructor
public class User {

    @Comment("사용자 식별자")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @Comment("닉네임")
    @Column(unique = true)
    private String nickname;

    @Comment("게임 서버")
    private String server;

    @Comment("게임 캐릭터 레벨")
    private short level;

    @Comment("게임 캐릭터 직업")
    private String job;

    @Comment("보유 골드")
    private long gold;

    @Column(unique = true)
    private String email;
    private String password;

    public void validateGold(long totalPrice) {
        if (getGold() < totalPrice) {
            throw new CustomException(ErrorCode.LACK_OF_GOLD);
        }
    }
}
