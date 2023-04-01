package prettypop.shop.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import prettypop.shop.dto.MemberBasicDto;
import prettypop.shop.dto.QMemberBasicDto;
import prettypop.shop.entity.Member;

import javax.persistence.EntityManager;

import java.util.Optional;

import static prettypop.shop.entity.QMember.member;
import static prettypop.shop.entity.QOrder.order;
import static prettypop.shop.entity.QReview.review;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory query;

    public MemberRepositoryCustomImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Optional<MemberBasicDto> findBasicInfoById(Long id) {
        MemberBasicDto memberBasicDto = query
                .select(new QMemberBasicDto(
                        member.username,
                        member.name,
                        member.gender,
                        member.birthDate,
                        member.nickname,
                        member.address,
                        member.phoneNumber,
                        member.email,
                        member.point,
                        order.countDistinct().intValue(),
                        review.countDistinct().intValue()
                ))
                .from(member)
                .leftJoin(member.reviews, review)
                .leftJoin(member.orders, order)
                .where(member.id.eq(id))
                .groupBy(member.id)
                .fetchOne();
        return Optional.of(memberBasicDto);
    }
}
