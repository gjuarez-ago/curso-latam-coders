package com.services.curso.domain.response;

import java.util.Date;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class HttpResponse {
	
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "America/New_York")
	    private Date timeStamp;
	    private int httpStatusCode; // 200, 201, 400, 500
	    private HttpStatus httpStatus;
	    private String reason;
	    private String message;

	    // Constructor never used. Can be (and should be) deleted
	    public HttpResponse() {}

	    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
	        this.timeStamp = new Date();
	        this.httpStatusCode = httpStatusCode;
	        this.httpStatus = httpStatus;
	        this.reason = reason;
	        this.message = message;
	    }


}
