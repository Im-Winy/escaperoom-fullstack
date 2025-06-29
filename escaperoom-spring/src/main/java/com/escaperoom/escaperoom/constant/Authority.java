package com.escaperoom.escaperoom.constant;

//Classe qui contient les permissions (authorities) associées aux rôles.
public class Authority {

 // Utilisateur : peut lire des données
 public static final String[] USER_AUTHORITIES = { "user:read" };

 // Admin : peut lire, mettre à jour, créer et supprimmer
 public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create", "user:delete" };

}