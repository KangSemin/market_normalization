package no.gunbang.market.domain.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.domain.auction.dto.AuctionHistoryResponseDto;
import no.gunbang.market.domain.market.dto.MarketHistoryResponseDto;
import no.gunbang.market.domain.user.dto.UserResponseDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public void login(){

    }

    public void logout(){

    }

    public UserResponseDto getUser() {
        return null;
    }

    public List<MarketHistoryResponseDto> getMarketHistory() {
        return null;
    }

    public List<AuctionHistoryResponseDto> getAuctionHistory() {
        return null;
    }
}
