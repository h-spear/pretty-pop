package prettypop.shop.repository.custom;

import prettypop.shop.dto.MemberDto;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<MemberDto> findBasicInfoById(Long id);
}
