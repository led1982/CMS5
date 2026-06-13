package com.company.cms.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "department"})
    @Query("select u from UserAccount u where u.id = :id")
    Optional<UserAccount> findDetailedById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"roles", "department"})
    @Query("""
        select distinct u from UserAccount u
        left join u.roles r
        where (:q is null or lower(u.displayName) like lower(concat('%', :q, '%'))
            or lower(u.email) like lower(concat('%', :q, '%')))
        and (:role is null or r.code = :role)
        order by u.displayName asc
    """)
    List<UserAccount> search(@Param("q") String q, @Param("role") RoleCode role);

    @EntityGraph(attributePaths = {"roles", "department"})
    List<UserAccount> findByStatus(UserStatus status);
}
