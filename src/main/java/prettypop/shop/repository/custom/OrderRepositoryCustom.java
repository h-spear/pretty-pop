package prettypop.shop.repository.custom;

import prettypop.shop.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findAllByMemberAndOrderDate(Long memberId, int year, int month);
}
