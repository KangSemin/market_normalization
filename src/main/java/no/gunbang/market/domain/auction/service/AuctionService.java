package no.gunbang.market.domain.auction.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.ItemRepository;
import no.gunbang.market.common.Status;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.dto.request.AuctionRegistrationRequestDto;
import no.gunbang.market.domain.auction.dto.request.BidAuctionRequestDto;
import no.gunbang.market.domain.auction.dto.response.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.response.AuctionRegistrationResponseDto;
import no.gunbang.market.domain.auction.dto.response.AuctionResponseDto;
import no.gunbang.market.domain.auction.dto.response.BidAuctionResponseDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.entity.Bid;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import no.gunbang.market.domain.auction.repository.BidRepository;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuctionService {

    private static final LocalDateTime START_DATE = LocalDateTime.now().minusDays(30);

    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    public Page<AuctionListResponseDto> getPopulars(Pageable pageable) {
        return auctionRepository.findPopularAuctionItems(
            START_DATE,
            pageable
        );
    }

    public Page<AuctionListResponseDto> getAllAuctions(
        Pageable pageable,
        String searchKeyword,
        String sortBy,
        String sortDirection
    ) {
        return auctionRepository.findAllAuctionItems(
            START_DATE,
            searchKeyword,
            sortBy,
            sortDirection,
            pageable
        );
    }

    public AuctionResponseDto getAuctionById(Long auctionId) {

        Auction foundAuction = findAuctionById(auctionId);

        Optional<Bid> currentBid = bidRepository.findByAuction(foundAuction);

        long currentMaxPrice = currentBid.map(Bid::getBidPrice)
            .orElse(foundAuction.getStartingPrice());

        return AuctionResponseDto.toDto(
            foundAuction,
            currentMaxPrice
        );
    }

    @Transactional
    public AuctionRegistrationResponseDto registerAuction(
        Long userId,
        AuctionRegistrationRequestDto requestDto
    ) {
        User foundUser = findUserById(userId);

        Long itemId = requestDto.getItemId();

        Item foundItem = findItemByItem(itemId);

        Auction auctionToRegister = Auction.of(
            foundUser,
            foundItem,
            requestDto.getStartingPrice(),
            requestDto.getAuctionDays()
        );

        Auction registeredAuction = auctionRepository.save(auctionToRegister);

        return AuctionRegistrationResponseDto.toDto(registeredAuction);
    }

    @Transactional
    public BidAuctionResponseDto bidAuction(
        Long userId,
        BidAuctionRequestDto requestDto
    ) {
        User foundUser = findUserById(userId);

        Long auctionId = requestDto.getAuctionId();

        Auction foundAuction = auctionRepository.findByIdAndStatus(
            auctionId,
            Status.ON_SALE
        ).orElseThrow(
            () -> new CustomException(ErrorCode.AUCTION_NOT_ACTIVE)
        );

        long bidPrice = requestDto.getBidPrice();

        Bid foundBid = bidRepository.findByAuction(foundAuction)
            .orElseGet(
                () -> Bid.of(
                    foundUser,
                    foundAuction,
                    bidPrice
                )
            );

        foundBid.updateBid(bidPrice, foundUser);

        foundAuction.incrementBidderCount();

        auctionRepository.save(foundAuction);

        return BidAuctionResponseDto.toDto(foundBid);
    }

    @Transactional
    public void deleteAuction(Long userId, Long auctionId) {

        boolean hasBid = bidRepository.existsByAuctionId(auctionId);

        if (hasBid) {
            throw new CustomException(ErrorCode.CANNOT_CANCEL_AUCTION);
        }

        Auction foundAuction = findAuctionById(auctionId);

        foundAuction.validateUser(userId);

        foundAuction.delete();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
    }

    private Item findItemByItem(Long itemId) {
        return itemRepository.findById(itemId)
            .orElseThrow(
                () -> new CustomException(ErrorCode.ITEM_NOT_FOUND)
            );
    }

    private Auction findAuctionById(Long auctionId) {
        return auctionRepository.findById(auctionId)
            .orElseThrow(
                () -> new CustomException(ErrorCode.AUCTION_NOT_FOUND)
            );
    }
}