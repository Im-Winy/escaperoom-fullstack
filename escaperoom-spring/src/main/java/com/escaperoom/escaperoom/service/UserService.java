package com.escaperoom.escaperoom.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.escaperoom.escaperoom.entity.User;

public interface UserService extends UserDetailsService {

    User register(String prenom, String nom, String username, String email);
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    
    User addNewUser(String prenom, String nom, String username, String password, String email, String role,
            boolean parseBoolean, boolean parseBoolean2);
    
    User updateUser(long idUser, String prenom, String nom, String username, String email, String role);
    
    User updateOneUser(long idUser, String prenom, String nom, String username, String email, String password);
    
    void deleteUser(long id);

}