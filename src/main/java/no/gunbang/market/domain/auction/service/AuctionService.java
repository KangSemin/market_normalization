package no.gunbang.market.domain.auction.service;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.ItemRepository;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.dto.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.request.CreateAuctionRequestDto;
import no.gunbang.market.domain.auction.dto.response.CreateAuctionResponseDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    public CreateAuctionResponseDto saveAuction(
        CreateAuctionRequestDto requestDto,
        Long userId
    ) {

        User foundUser = userRepository.findById(userId)
            .orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );

        Item foundItem = itemRepository.findById(requestDto.getItemId())
            .orElseThrow(
                () -> new CustomException(ErrorCode.ITEM_NOT_FOUND)
            );

        boolean isAuctionDaysOutOfRange = requestDto.getAuctionDays() < 3
            || 7 < requestDto.getAuctionDays();

        if (isAuctionDaysOutOfRange) {
            throw new CustomException(ErrorCode.AUCTION_DAYS_OUT_OF_RANGE);
        }

        Auction auctionToSave = Auction.of(
            foundUser,
            foundItem,
            requestDto.getStartingPrice(),
            requestDto.getAuctionDays()
        );

        Auction savedAuction = auctionRepository.save(auctionToSave);

        return CreateAuctionResponseDto.toDto(savedAuction);
    }

    public Page<AuctionListResponseDto> getPopulars(Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return auctionRepository.findPopularBidItems(startDate, pageable);
    }
}