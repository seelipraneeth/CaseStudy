package com.spotflock.casestudy.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.spotflock.casestudy.SpotFlockApplication;
import com.spotflock.casestudy.security.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class CustomJWTokenService {

	//Initialize slf4j
	private final Logger log = LoggerFactory.getLogger(CustomJWTokenService.class);
		
	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationInMs}")
	private String jwtExpirationInMs;

	public String generateToken(String username) {
		log.debug("CustomJWTokenService: Generating token for username: "+username);
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + Long.parseLong(jwtExpirationInMs));

		return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String getUserIdFromJWT(String token) {
		log.debug("CustomJWTokenService: Get userid from jwt token");
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String authToken) {
		try {
			log.info("CustomJWTokenService: Validating token");
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			if (SpotFlockApplication.blackListedTokens.contains(authToken)) {
				throw new ExpiredJwtException(null, null, "Session expired");
			}
			return true;
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature");
			ex.printStackTrace();
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
			ex.printStackTrace();
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
			ex.printStackTrace();
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
			ex.printStackTrace();
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
			ex.printStackTrace();
		}
		return false;
	}

	public String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		return getToken(bearerToken);
	}

	private String getToken(String bearerToken) {
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

}
