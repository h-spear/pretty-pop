package prettypop.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prettypop.shop.entity.Member;
import prettypop.shop.repository.custom.MemberRepositoryCustom;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

    @Query("select m from Member m join fetch m.roles where m.username = :username")
    Optional<Member> findByUsernameWithRoles(@Param("username") String username);
}
