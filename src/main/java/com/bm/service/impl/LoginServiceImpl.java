package com.bm.service.impl;

import com.bm.dto.LoginResponse;
import com.bm.dto.CustomUserDetails;
import com.bm.dto.LoginRequest;
import com.bm.entity.User;
import com.bm.exception.ErrorResponse;
import com.bm.exception.Errors;
import com.bm.service.LoginService;
import com.bm.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;

	public LoginServiceImpl(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
	}

	@Override
    public ResponseEntity<?> login(LoginRequest loginRequest) throws Exception {
		log.info("Trying to log-in for user with username={}", loginRequest.getEmail());
		String userName = loginRequest.getEmail();
		String password = loginRequest.getPassword();

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					userName,
					password
			));
		} catch (BadCredentialsException e) {
			log.error("Bad Credentials, Incorrect email or password");
			return new ResponseEntity<>(new ErrorResponse(Errors.UserNotFound.getErrorMessage()), HttpStatus.NOT_FOUND);
		}

		log.info("Authenticating and generating JWT for user with username={}", loginRequest.getEmail());
		CustomUserDetails customUserDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(userName);
		User user = customUserDetails.getUser();
		log.info("Generating JWT for Authorization");
		String jwt = jwtUtil.generateToken(customUserDetails);

		log.info("Logged in successfully");
		return new ResponseEntity<>(new LoginResponse(user.getName(), user.getEmail(), jwt), HttpStatus.OK);
	}
}
