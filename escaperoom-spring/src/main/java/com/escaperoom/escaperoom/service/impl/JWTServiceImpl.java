package com.escaperoom.escaperoom.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.escaperoom.escaperoom.constant.SecurityConstant;
import com.escaperoom.escaperoom.service.JWTService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements JWTService {

    // Clé secrète codée en base64 utilisée pour signer les tokens JWT
    private static final String SECRET_KEY = "413F4428472B4B6250655368566D5970337336763979244226452948404D6351";

    // Extrait le nom d'utilisateur (subject) à partir d'un token JWT
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Génère un token JWT en utilisant les autorités de l'utilisateur
    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(SecurityConstant.AUTHORITIES, userDetails.getAuthorities()), userDetails);
    }

    // Génère un token JWT avec des claims supplémentaires
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims) // données supplémentaires à inclure dans le token
                .setSubject(userDetails.getUsername()) // utilisateur concerné par le token
                .setIssuer(SecurityConstant.GET_ESCAPEROOM_ARRAYS) // émetteur du token
                .setAudience(SecurityConstant.GET_ADMINISTRATION_ARRAYS) // audience cible du token
                .setIssuedAt(new Date(System.currentTimeMillis())) // date de création du token
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstant.EXPIRATION_TIME)) // date d'expiration
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // signature avec la clé secrète
                .compact(); // création finale du token
    }

    @Override
    // Génère un token de rafraîchissement avec une expiration plus longue
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuer(SecurityConstant.GET_ESCAPEROOM_ARRAYS)
                .setAudience(SecurityConstant.GET_ADMINISTRATION_ARRAYS)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstant.REFRESH_EXPIRATION_TIME)) // durée plus longue ici
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Vérifie si un token est valide pour un utilisateur donné
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Vérifie si le token a expiré
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Méthode générique pour extraire un claim spécifique depuis un token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrait tous les claims du token JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // clé utilisée pour vérifier la signature
                .build()
                .parseClaimsJws(token) // analyse et vérifie le JWT
                .getBody(); // retourne le contenu (claims) du token
    }

    // Convertit la clé secrète en un objet Key utilisable pour signer/valider un JWT
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // décode la clé base64
        return Keys.hmacShaKeyFor(keyBytes); // génère une clé HMAC-SHA
    }
}
