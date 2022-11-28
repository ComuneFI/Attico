package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import it.linksmt.assatti.datalayer.domain.Utente;

/**
 * Spring Data JPA repository for the OAuthToken entity.
 */
public interface OAuthTokenRepository extends JpaRepository<Utente,Long> {
	@Modifying
	@Query(value="delete from oauth_access_token where user_name = ?1", nativeQuery=true)
	void deleteTokenByUsername(String username);
	
}
