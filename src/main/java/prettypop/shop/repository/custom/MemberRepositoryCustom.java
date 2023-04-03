package prettypop.shop.repository.custom;

import prettypop.shop.dto.member.MemberDto;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<MemberDto> findBasicInfoById(Long id);
}
