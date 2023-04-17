package prettypop.shop.restcontroller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import prettypop.shop.configuration.annotation.Login;
import prettypop.shop.exception.CannotWriteReviewException;
import prettypop.shop.restcontroller.request.ReviewCreateRequest;
import prettypop.shop.restcontroller.request.ReviewUpdateRequest;
import prettypop.shop.restcontroller.response.ApiResponse;
import prettypop.shop.service.ReviewService;

@Slf4j
@RequiredArgsConstructor
@Api(tags = "review", description = "리뷰")
@RestController
@RequestMapping("/items")
public class ReviewRestController {

    private final ReviewService reviewService;

    @ApiOperation(value = "상품에 리뷰 작성 가능 여부 반환")
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

    @ApiOperation(value = "상품에 리뷰 작성")
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

    @ApiOperation(value = "상품에 작성된 리뷰 수정")
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

    @ApiOperation(value = "상품에 리뷰 작성한 것으로 처리")
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

    @ApiOperation(value = "상품에 작성된 리뷰 삭제")
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
