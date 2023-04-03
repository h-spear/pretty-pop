package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import prettypop.shop.dto.item.ItemQueryCondition;
import prettypop.shop.service.ItemService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public String items(@ModelAttribute ItemQueryCondition condition,
                        @PageableDefault(size = 12) Pageable pageable,
                        Model model) {

        log.info("condition={}", condition);
        log.info("pageable={}", pageable);
        model.addAttribute("itemQueryResults", itemService.query(condition, pageable));
        return "shop/item/itemList";
    }

    @GetMapping("/{id}")
    public String itemView(@PathVariable Long id,
                           Model model) {
        log.info("아이템 뷰 페이지 이동 id={}", id);
        model.addAttribute("item", itemService.findItemWithReviews(id));
        model.addAttribute("nlString", System.getProperty("line.separator").toString());
        return "shop/item/itemView";
    }
}
