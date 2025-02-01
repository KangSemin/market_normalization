package no.gunbang.market.domain.auction.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import no.gunbang.market.common.Item;
import no.gunbang.market.common.ItemRepository;
import no.gunbang.market.common.Status;
import no.gunbang.market.common.exception.CustomException;
import no.gunbang.market.common.exception.ErrorCode;
import no.gunbang.market.domain.auction.dto.response.AuctionListResponseDto;
import no.gunbang.market.domain.auction.dto.request.CreateAuctionRequestDto;
import no.gunbang.market.domain.auction.dto.response.CreateAuctionResponseDto;
import no.gunbang.market.domain.auction.entity.Auction;
import no.gunbang.market.domain.auction.repository.AuctionRepository;
import no.gunbang.market.domain.user.entity.User;
import no.gunbang.market.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    public Page<AuctionListResponseDto> getPopulars(Pageable pageable) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return auctionRepository.findPopularAuctionItems(startDate, pageable);
    }

    public Page<AuctionListResponseDto> getAllAuctions(Pageable pageable) {
        return auctionRepository.findAllAuctionItems(pageable);
    }

    @Transactional
    public CreateAuctionResponseDto saveAuction(
        CreateAuctionRequestDto requestDto,
        Long userId
    ) {
        User foundUser = findUserById(userId);

        Long itemId = requestDto.getItemId();

        Item foundItem = findItemByItem(itemId);

        Auction auctionToSave = Auction.of(
            foundUser,
            foundItem,
            requestDto.getStartingPrice(),
            requestDto.getAuctionDays()
        );

        Auction savedAuction = auctionRepository.save(auctionToSave);

        return CreateAuctionResponseDto.toDto(savedAuction);
    }

    @Transactional
    public void deleteAuction(Long userId, Long auctionId) {
        User foundUser = findUserById(userId);

        Auction foundAuction = findAuctionById(auctionId);

        boolean isUserDifferent = foundUser.getId().equals(foundAuction.getUser().getId());

        if (isUserDifferent) {
            throw new CustomException(ErrorCode.USER_DIFFERENT);
        }

        if (foundAuction.getStatus() != Status.ON_SALE) {
            throw new CustomException(ErrorCode.CANNOT_CANCEL_AUCTION);
        }

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