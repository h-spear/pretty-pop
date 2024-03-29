package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import prettypop.shop.service.ItemService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ItemService itemService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("topItems", itemService.findTopItems(8));
        model.addAttribute("items", itemService.findTopItemsByCategory(10));
        return "home";
    }

    @GetMapping("/company")
    public String company() {
        return "common/company";
    }

    @GetMapping("/notice")
    public String notice() {
        return "common/notice";
    }

//    @GetMapping("/cs-center")
    public String csCenter() {
        return "csCenter";
    }

    @GetMapping("/use")
    public String use() {
        return "common/use";
    }
}

