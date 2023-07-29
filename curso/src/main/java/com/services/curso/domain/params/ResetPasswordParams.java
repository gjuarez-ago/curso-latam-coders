package com.services.curso.domain.params;

import lombok.Data;

@Data
public class ResetPasswordParams {

   private String email;
	
	private String token;
	
	private String password;
	
}
