package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import prettypop.shop.dto.ItemQueryCondition;
import prettypop.shop.service.ItemService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public String items(@ModelAttribute ItemQueryCondition condition,
                        Pageable pageable,
                        Model model) {

        model.addAttribute("itemQueryResults", itemService.query(condition, pageable));
        return "shop/item/itemList";
    }
}
