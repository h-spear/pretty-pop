package prettypop.shop.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import prettypop.shop.entity.Order;

import javax.persistence.EntityManager;
import java.util.List;

import static prettypop.shop.entity.QItem.item;
import static prettypop.shop.entity.QOrder.order;
import static prettypop.shop.entity.QOrderItem.orderItem;

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory query;

    public OrderRepositoryCustomImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public List<Order> findAllByMemberAndOrderDate(Long memberId, int year, int month) {
        return query
                .select(order).distinct()
                .from(order)
                .join(order.orderItems, orderItem).fetchJoin()
                .join(orderItem.item, item).fetchJoin()
                .where(order.member.id.eq(memberId),
                        order.orderDate.year().eq(year),
                        order.orderDate.month().eq(month))
                .orderBy(order.orderDate.desc())
                .fetch();
    }
}
