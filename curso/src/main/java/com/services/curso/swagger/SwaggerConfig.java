package com.services.curso.swagger;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SwaggerConfig {
	
	public static final String AUTHORIZATION_HEADER = "Authorization";

    private ApiKey apiKey(){
          return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
   }
   
   @Bean
   public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metaData())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.services.curso.controller"))
                .paths(PathSelectors.any())
                .build();
   }
	 
//	 @Bean
//	 public Docket api() {
//	        return new Docket(DocumentationType.SWAGGER_2)
//	                .select()
//	                .apis(RequestHandlerSelectors.basePackage("com.services.ago.planogram.controller"))
//	                .paths(PathSelectors.any())
//	                .build().apiInfo(metaData());
//	 }

	 private ApiInfo metaData() {
	        return new ApiInfoBuilder()
	                .title("AGO Consultores API ")
	                .description("Ago consultores")
	                .version("1.1.0")
	                .license("Apache 2.0")
	                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
	                .contact(new Contact("support.team@agoconsultores.com.mx", "AGO Consultores","agoconsultores@agoconsultores.com.mx"))
	                .build();
	 }
	   
	 private SecurityContext securityContext(){
          return SecurityContext.builder().securityReferences(defaultAuth()).build();
     }

      private List<SecurityReference> defaultAuth(){
          AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
          AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
          authorizationScopes[0] = authorizationScope;
          return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
      }
      
	 @Bean
	 public WebMvcConfigurer webMvcConfigurer()
	 {
	        return new WebMvcConfigurer()
	        {
	            @Override
	            public void addResourceHandlers( ResourceHandlerRegistry registry )
	            {
	                registry.addResourceHandler( "swagger-ui.html" ).addResourceLocations( "classpath:/META-INF/resources/" );
	                registry.addResourceHandler( "/webjars/**" ).addResourceLocations( "classpath:/META-INF/resources/webjars/" );
	            }
	        };
	 }

}