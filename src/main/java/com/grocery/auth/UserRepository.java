package com.grocery.auth;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	
	@Query("""
		    SELECT u FROM User u
		    WHERE (:city = '' OR LOWER(u.city) LIKE LOWER(CONCAT('%', :city, '%')))
		    AND (
		            :search = '' OR 
		            LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            u.mobile LIKE CONCAT('%', :search, '%'))
		""")
		Page<User> searchUsers(
		        @Param("city") String city,
		        @Param("search") String search,
		        Pageable pageable);
}
