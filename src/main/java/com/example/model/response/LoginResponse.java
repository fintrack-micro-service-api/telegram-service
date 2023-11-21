package com.example.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
	
	private String access_token;
	private String refresh_token;
	private String expires_in;
	private String refresh_expires_in;
	private String token_type;
	private String username;


}
