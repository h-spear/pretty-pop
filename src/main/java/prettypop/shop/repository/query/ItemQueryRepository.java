package prettypop.shop.repository.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import prettypop.shop.dto.item.ItemQueryCondition;
import prettypop.shop.dto.item.ItemQueryDto;
import prettypop.shop.dto.item.ItemQueryOrder;
import prettypop.shop.dto.item.QItemQueryDto;
import prettypop.shop.entity.Category;
import prettypop.shop.entity.Item;

import javax.persistence.EntityManager;
import java.util.List;

import static prettypop.shop.entity.QItem.item;
import static prettypop.shop.entity.QReview.review;

@Repository
public class ItemQueryRepository {

    private final JPAQueryFactory query;

    public ItemQueryRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public Page<ItemQueryDto> query(ItemQueryCondition condition, Pageable pageable) {
        List<ItemQueryDto> result = query
                .select(new QItemQueryDto(
                        item.id,
                        item.name,
                        item.thumbnailImageUrl,
                        item.originalPrice,
                        item.purchasePrice,
                        item.earnedPoint,
                        item.stockQuantity,
                        item.itemStatus,
                        item.category,
                        review.rating.avg().as("rating"),
                        review.count().intValue().as("reviewCount")))
                .from(item)
                .leftJoin(item.reviews, review)
                .where(stringContains(item.name, condition.getKeyword()),
                        categoryEqual(condition.getCategory()),
                        priceGe(condition.getPriceGe()),
                        priceLe(condition.getPriceLe())
                        )
                .groupBy(item)
                .having(ratingGe(condition.getRatingGe()),
                        reviewCountGe(condition.getReviewCountGe()))
                .orderBy(getOrderSpecifier(condition.getOrder()),
                        review.rating.avg().desc(),
                        item.registrationDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Item> countQuery = query
                .select(item)
                .from(item)
                .leftJoin(item.reviews, review)
                .where(stringContains(item.name, condition.getKeyword()),
                        categoryEqual(condition.getCategory()),
                        priceGe(condition.getPriceGe()),
                        priceLe(condition.getPriceLe())
                )
                .groupBy(item)
                .having(ratingGe(condition.getRatingGe()),
                        reviewCountGe(condition.getReviewCountGe()));

        return PageableExecutionUtils.getPage(result, pageable, () -> countQuery.fetchCount());
    }

    private BooleanExpression ratingGe(Integer ratingGeCond) {
        return ratingGeCond != null ? review.rating.avg().goe(ratingGeCond) : null;
    }

    private BooleanExpression reviewCountGe(Integer reviewCountGeCond) {
        return reviewCountGeCond != null ? review.count().goe(reviewCountGeCond) : null;
    }

    private BooleanExpression priceGe(Integer priceGeCond) {
        return priceGeCond != null ? item.purchasePrice.goe(priceGeCond) : null;
    }

    private BooleanExpression priceLe(Integer priceLeCond) {
        return priceLeCond != null ? item.purchasePrice.loe(priceLeCond) : null;
    }

    private BooleanExpression categoryEqual(Category category) {
        return category != null ? item.category.eq(category) : null;
    }

    private OrderSpecifier<?> getOrderSpecifier(ItemQueryOrder order) {
        if (order == ItemQueryOrder.PRICE_ASC) {
            return item.purchasePrice.asc();
        } else if (order == ItemQueryOrder.PRICE_DESC) {
            return item.purchasePrice.desc();
        } else if (order == ItemQueryOrder.REVIEWS_DESC) {
            return review.count().desc();
        } else if (order == ItemQueryOrder.RATING_DESC) {
            return review.rating.avg().desc();
        } else if (order == ItemQueryOrder.NEWEST) {
            return item.registrationDate.desc();
        } else if (order == ItemQueryOrder.SALES_VOLUME){
            return item.salesVolume.desc();
        } else {
            return review.rating.avg().desc();
        }
    }

    private BooleanExpression stringContains(StringExpression expression, String pattern) {
        if (expression == null || !StringUtils.hasText(pattern)) {
            return null;
        }
        return expression.lower().contains(pattern.toLowerCase());
    }
}
