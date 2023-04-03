package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.*;
import prettypop.shop.entity.CartItem;
import prettypop.shop.entity.Item;
import prettypop.shop.entity.Member;
import prettypop.shop.entity.WishItem;
import prettypop.shop.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ItemQueryRepository itemQueryRepository;
    private final CartItemRepository cartItemRepository;
    private final WishItemRepository wishItemRepository;

    public Page<ItemQueryDto> query(ItemQueryCondition condition, Pageable pageable) {
        return itemQueryRepository.query(condition, pageable);
    }

    public ItemDto findItemWithReviews(Long id) {
        return ItemDto.of(itemRepository.findByIdWithReviews(id)
                .orElseThrow(IllegalArgumentException::new));
    }

    public List<OrderItemDto> getOrderItems(List<ItemCountRequest> itemRequests) {
        Map<Long, Integer> itemQuantityMap = new HashMap<>();
        List<Long> itemIds = new ArrayList<>();
        extractItemRequest(itemRequests, itemQuantityMap, itemIds);

        return itemRepository.findAllById(itemIds).stream()
                .map(item -> OrderItemDto.of(item, itemQuantityMap.get(item.getId())))
                .collect(Collectors.toList());
    }

    private void extractItemRequest(List<ItemCountRequest> itemRequests, Map<Long, Integer> itemQuantityMap, List<Long> itemIds) {
        for (ItemCountRequest itemRequest: itemRequests) {
            Long itemId = itemRequest.getItemId();
            int quantity = itemRequest.getQuantity();
            itemIds.add(itemId);
            itemQuantityMap.put(itemId, quantity);
        }
    }

    @Transactional
    public void addCartItem(Long memberId, Long itemId, int count) {
        log.info("장바구니 아이템 추가 로직 실행");
        Optional<CartItem> optional = cartItemRepository.findByMemberAndItem(memberId, itemId);
        if (optional.isEmpty()) {
            Member member = findMember(memberId);
            Item item = findItem(itemId);
            CartItem cartItem = cartItemRepository.save(new CartItem(member, item, count));
            member.addCartItem(cartItem);
        } else {
            CartItem cartItem = optional.get();
            cartItem.addCount(count);
        }
    }

    @Transactional
    public void deleteCartItem(Long memberId, Long itemId) {
        cartItemRepository.findByMemberAndItem(memberId, itemId)
                .ifPresentOrElse(
                        cartItem -> cartItemRepository.delete(cartItem),
                        () -> {}
                );
    }

    @Transactional
    public void updateCartItemQuantity(Long memberId, Long itemId, int quantity) {
        log.info("장바구니 아이템 개수 변경 로직 실행");
        if (quantity <= 0) {
            throw new IllegalArgumentException("0개 이하로 개수롤 지정할 수 없습니다.");
        }
        CartItem cartItem = cartItemRepository.findByMemberAndItem(memberId, itemId)
                .orElseThrow(IllegalArgumentException::new);

        cartItem.setCount(quantity);
    }

    public List<CartItemDto> getCartList(Long memberId) {
        return cartItemRepository.findAllByMemberId(memberId).stream()
                .map(cartItem -> CartItemDto.of(cartItem))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addWish(Long memberId, Long itemId) {
        log.info("찜 목록 아이템 추가 로직 실행");
        Member member = findMember(memberId);
        Item item = findItem(itemId);

        try {
            wishItemRepository.findByMemberAndItem(memberId, itemId).orElseThrow();
        } catch (NoSuchElementException e) {
            WishItem wishItem = wishItemRepository.save(new WishItem(member, item));
            member.addWishItem(wishItem);
        }
    }

    @Transactional
    public void deleteWish(Long memberId, Long itemId) {
        log.info("찜 목록 아이템 삭제 로직 실행");
        WishItem wishItem = wishItemRepository.findByMemberAndItem(memberId, itemId)
                .orElseThrow(IllegalArgumentException::new);
        wishItemRepository.delete(wishItem);
    }

    public List<WishItemDto> getWishList(Long memberId) {
        return wishItemRepository.findByMemberId(memberId).stream()
                .map(wishItem -> WishItemDto.of(wishItem))
                .collect(Collectors.toList());
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId).
                orElseThrow(IllegalArgumentException::new);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);
    }
}
