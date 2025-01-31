package no.gunbang.market.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "`user`")
@Getter
public class User {

    @Comment("사용자 식별자")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @Comment("닉네임")
    private String nickname;

    @Comment("게임 서버")
    private String server;

    @Comment("게임 캐릭터 레벨")
    private short level;

    @Comment("게임 캐릭터 직업")
    private String job;

    @Comment("보유 골드")
    private long gold;
}
