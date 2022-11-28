package it.linksmt.assatti.datalayer.repository;

import it.linksmt.assatti.datalayer.domain.User;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(DateTime dateTime);

    User findOneByResetKey(String resetKey);

    User findOneByLogin(String login);

    User findOneByEmail(String email);
    
    @Modifying
	@Query(value="delete from jhi_user_authority where user_id = ?1", nativeQuery=true)
	public void deleteAuthoritiesOfUser(Long userId);
    
    @Modifying
	@Query(value="insert into jhi_user_authority(user_id, authority_name) values (?1, ?2)", nativeQuery=true)
	public void addAuthorityToUser(Long userId, String authorityName);
    
    @Query(value="select distinct login from jhi_user", nativeQuery=true)
	List<String> findAllLoginId();

}
