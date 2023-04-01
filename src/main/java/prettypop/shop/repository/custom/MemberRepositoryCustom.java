package prettypop.shop.repository.custom;

import prettypop.shop.dto.MemberBasicDto;
import prettypop.shop.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<MemberBasicDto> findBasicInfoById(Long id);
}
