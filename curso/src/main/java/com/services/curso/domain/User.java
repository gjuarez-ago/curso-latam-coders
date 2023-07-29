package com.services.curso.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class User implements Serializable{

    private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Long id;
	
	@Column(nullable = false)
	private String names;
	
	@Column(nullable = false)
	private String surnames;
	
	@Column(nullable = false)
	private String numberPhone;
	
	@Column(nullable = false)	
	private String username;
	
	@Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
    private String profileImageUrl;
	
	private String token;
	
	private Date expireToken;
	
	@Column(nullable = false)
	private String role;
	
	private Date joinDate;
	
    private Date lastLoginDateDisplay;
	
    
	private String[] authorities;
	
	private boolean isActive;
	
	private boolean isNotLocked;
	
    private Date lastLoginDate;
    
	@JsonProperty(access = Access.WRITE_ONLY)
	private Date regDateCreated;

	@JsonProperty(access = Access.WRITE_ONLY)
	private Long regCreatedBy;

	@JsonProperty(access = Access.WRITE_ONLY)
	private Date regDateUpdated;

	@JsonProperty(access = Access.WRITE_ONLY)
	private Long regUpdateBy;

}
