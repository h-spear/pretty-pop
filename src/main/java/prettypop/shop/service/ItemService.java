package prettypop.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prettypop.shop.dto.ItemDto;
import prettypop.shop.dto.ItemQueryCondition;
import prettypop.shop.dto.ItemQueryDto;
import prettypop.shop.repository.ItemQueryRepository;
import prettypop.shop.repository.ItemRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemQueryRepository itemQueryRepository;

    public Page<ItemQueryDto> query(ItemQueryCondition condition, Pageable pageable) {
        return itemQueryRepository.query(condition, pageable);
    }

    public ItemDto findOne(Long id) {
        return ItemDto.of(itemRepository.findByIdWithReviews(id)
                .orElseThrow(IllegalArgumentException::new));
    }
}
