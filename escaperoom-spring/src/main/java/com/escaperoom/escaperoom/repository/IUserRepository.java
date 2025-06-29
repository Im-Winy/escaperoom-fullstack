package com.escaperoom.escaperoom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escaperoom.escaperoom.entity.User;
import com.escaperoom.escaperoom.entity.Role;


@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
	
	User findUserByEmail(String email);
	User findByRole(Role role);
	User findUserByUsername(String username);
	User findById(String id);
}
