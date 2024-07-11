package com.user.management.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.user.management.exception.UserAuthApplicationException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
	
	Logger logger= LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${app.jwt-secret}")
	private String jwtSecret;

	@Value("${app-jwt-expiration-milliseconds}")
	private long jwtExpirationDate;
	
	public String generateJwtToken(Authentication authentication) {
		
		String username = authentication.getName();
		
		Date issuedDate =new Date();
		Date expiryDate =new Date(issuedDate.getTime() + jwtExpirationDate);
		
		String token = Jwts.builder()
				.subject(username)
				.issuedAt(new Date())
				.expiration(expiryDate)
				.signWith(key())
				.compact();
		
		return token;
	}
	
	
	private Key key() {
	   return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
	
	// get username by token
	public String getUsername(String token) {
		
		return Jwts
				.parser()
				.verifyWith((SecretKey) key())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
			
	}
	
	public boolean validateToken(String token) {
		
		try {
			
			Jwts.parser().verifyWith((SecretKey) key()).build().parse(token);
			
			return true;
			
		} catch (MalformedJwtException malformedJwtException) {
			logger.error("Invalid Jwt token: {}",malformedJwtException.getMessage());
			throw new UserAuthApplicationException("Invalid Jwt token");
		} catch (ExpiredJwtException expiredJwtException) {
			logger.error("Expired JWT token: {}",expiredJwtException.getMessage());
			throw new UserAuthApplicationException("Expired JWT token");
		} catch (UnsupportedJwtException unsupportedJwtException) {
			logger.error("Unsupported JWT token: {}",unsupportedJwtException.getMessage());
			throw new UserAuthApplicationException("Unsupported JWT token");
		} catch (IllegalArgumentException illegalArgumentException) {
			logger.error("Jwt claims string is null or empty: {}",illegalArgumentException.getMessage());
			throw new UserAuthApplicationException("Jwt claims string is null or empty");
		}
		
	}
	
	
}
