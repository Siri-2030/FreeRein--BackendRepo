package com.hr.freerein.service;

import com.hr.freerein.entity.User;
import com.hr.freerein.exception.ResourceNotFoundException;
import com.hr.freerein.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/** Resolves the logged-in user and answers "may this user see/change that record?". */
@Service
public class AccessService {

    private final UserRepository userRepository;

    public AccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User currentUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /** Staff = people who help victims: admin, counsellors, legal advisors. */
    public boolean isStaff(User u) {
        return u.getRole() == User.Role.ADMIN
                || u.getRole() == User.Role.COUNSELLOR
                || u.getRole() == User.Role.LEGAL_ADVISOR;
    }

    public boolean isAdmin(User u) {
        return u.getRole() == User.Role.ADMIN;
    }

    public boolean isOwner(User u, User owner) {
        return owner != null && owner.getId() != null && owner.getId().equals(u.getId());
    }

    public void requireOwnerOrStaff(User u, User owner) {
        if (!isOwner(u, owner) && !isStaff(u)) {
            throw new AccessDeniedException("Not allowed");
        }
    }
}
