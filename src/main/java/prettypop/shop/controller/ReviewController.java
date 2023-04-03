package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.service.ReviewService;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String reviews() {
        return "redirect:/review/can-write";
    }

    @GetMapping("/written")
    public String reviewsWritten(@Login Long id,
                                 Model model) {
        log.info("작성한 리뷰 컨트롤러 실행");
        model.addAttribute("reviews", reviewService.findAllReviews(id));
        return "member/review/reviewsWritten";
    }

    @GetMapping("/can-write")
    public String reviewsCanWrite(@Login Long id,
                                  Model model) {
        log.info("작성할 수 있는 리뷰 컨트롤러 실행");
        model.addAttribute("items", reviewService.findCanReviewItems(id));
        return "member/review/reviewsCanWrite";
    }
}
