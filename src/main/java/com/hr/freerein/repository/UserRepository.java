package com.hr.freerein.repository;


import com.hr.freerein.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndActiveTrue(String email);
    
    boolean existsByEmail(String email);
    boolean existsByEmailAndActiveTrue(String email);
    
    // ✅ Role-based queries
    List<User> findByRoleAndActiveTrue(User.Role role);
    List<User> findByActiveTrue();
}

