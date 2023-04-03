package prettypop.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.controller.request.ReviewCreateRequest;
import prettypop.shop.controller.request.ReviewUpdateRequest;
import prettypop.shop.controller.response.ApiResponse;
import prettypop.shop.exception.CannotWriteReviewException;
import prettypop.shop.service.ReviewService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ReviewApiController {

    private final ReviewService reviewService;

    @GetMapping("/{itemId}/review")
    public ApiResponse canReview(@Login Long id,
                                 @PathVariable("itemId") Long itemId) {
        boolean canReview = reviewService.canReview(id, itemId);
        log.info("itemId={}", itemId);
        if (canReview) {
            return ApiResponse.ofSuccess();
        }
        return ApiResponse.ofError("상품평은 상품 구매 후에 작성할 수 있습니다.");
    }

    @PostMapping("/{itemId}/review")
    public ApiResponse review(@Login Long id,
                              @PathVariable("itemId") Long itemId,
                              @RequestBody ReviewCreateRequest createRequest) {
        try {
            reviewService.writeReview(id, itemId, createRequest.getRating(), createRequest.getContent());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("회원 번호 혹은 리뷰 번호가 잘못되었습니다.");
        } catch (CannotWriteReviewException e) {
            return ApiResponse.ofError("상품평은 상품 구매 후에 작성할 수 있습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @PutMapping("/{itemId}/review/{reviewId}")
    public ApiResponse modifyReview(@Login Long id,
                                    @PathVariable("itemId") Long itemId,
                                    @PathVariable("reviewId") Long reviewId,
                                    @RequestBody ReviewUpdateRequest updateRequest) {
        try {
            reviewService.modifyReview(id, reviewId, updateRequest.getRating(), updateRequest.getContent());
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("회원 번호 혹은 리뷰 번호가 잘못되었습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @PutMapping("/{itemId}/review")
    public ApiResponse doNotReview(@Login Long id,
                                   @PathVariable("itemId") Long itemId) {
        try {
            reviewService.updateHasReviewTrue(id, itemId);
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("회원 번호 혹은 상품 번호가 잘못되었습니다.");
        }
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("/{itemId}/review/{reviewId}")
    public ApiResponse deleteReview(@Login Long id,
                                    @PathVariable("itemId") Long itemId,
                                    @PathVariable("reviewId") Long reviewId) {
        try {
            reviewService.deleteReview(id, reviewId);
        } catch (IllegalArgumentException e) {
            return ApiResponse.ofError("회원 번호 혹은 리뷰 번호가 잘못되었습니다.");
        }
        return ApiResponse.ofSuccess();
    }
}
