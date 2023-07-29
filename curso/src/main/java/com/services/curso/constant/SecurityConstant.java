package com.services.curso.constant;


public class SecurityConstant {
	
	public static final long EXPIRATION_TIME = 432_000_000; // 5 days expressed in milliseconds
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String JWT_TOKEN_HEADER = "Jwt-Token";
	public static final String TOKEN_CANNOT_BE_VERIFIED = "El token no se puede verificar";
	public static final String GET_ARRAYS_LLC = "Empresa de Consultores, S.A de C.V";
	public static final String GET_ARRAYS_ADMINISTRATION = "Administración de usuarios";
	public static final String AUTHORITIES = "authorities";
	public static final String FORBIDDEN_MESSAGE = "Necesitas iniciar sesión para acceder a este servicio";
	public static final String ACCESS_DENIED_MESSAGE = "Usted no tiene permiso para acceder a este servicio";
	public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
 // public static final String[] PUBLIC_URLS = { "/user/login", "/user/register", "/user/image/**", "/swagger-ui.html/**", "/v2/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**" };
	public static final String[] PUBLIC_URLS = { "**" };

}
