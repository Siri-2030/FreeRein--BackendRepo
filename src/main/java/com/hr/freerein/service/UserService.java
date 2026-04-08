package com.hr.freerein.service;

import com.hr.freerein.entity.User;
import com.hr.freerein.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService extends BaseEntityService<User, Long, UserRepository> {  // ✅ Long ID

    public UserService(UserRepository repository) {
        super(repository);
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);  // ✅ Direct call
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public Optional<User> getCurrentUser(String email) {
        return findByEmail(email);
    }

    public List<User> findActiveCounsellors() {
        return repository.findByRoleAndActiveTrue(User.Role.COUNSELLOR);
    }
}