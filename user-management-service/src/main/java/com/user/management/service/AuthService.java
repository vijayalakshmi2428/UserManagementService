package com.user.management.service;

import com.user.management.payload.LoginDto;

public interface AuthService {

	String login(LoginDto loginDto);
}
