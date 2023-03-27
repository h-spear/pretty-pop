package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prettypop.shop.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);
}
