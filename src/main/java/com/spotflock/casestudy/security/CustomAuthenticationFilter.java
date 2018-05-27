package com.spotflock.casestudy.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spotflock.casestudy.controller.FriendController;
import com.spotflock.casestudy.service.CustomJWTokenService;

public class CustomAuthenticationFilter extends OncePerRequestFilter {

	//Initialize slf4j
	private final Logger log = LoggerFactory.getLogger(CustomAuthenticationFilter.class);
		
	@Autowired
	private CustomJWTokenService tokenService;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			log.info("CustomAuthenticationFilter: Filtering requests and validating token");
			String jwt = tokenService.getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && tokenService.validateToken(jwt)) {
				String username = tokenService.getUserIdFromJWT(jwt);

				UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails.getUsername(), null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				log.info("CustomAuthenticationFilter: Set context to spring security for authentication");
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ex) {
			logger.error("Could not set user authentication in security context", ex);
		}
		filterChain.doFilter(request, response);
	}
}