package com.services.curso.domain.params;

import lombok.Data;

@Data
public class UserParams {
	
	private String names;
	
	private String surnames;
		
	private String numberPhone;
	
	private String username;
	
	private String role;
		
	private boolean isActive;
	
	private boolean isNotLocked;
	
	private String imageBase64;

}
