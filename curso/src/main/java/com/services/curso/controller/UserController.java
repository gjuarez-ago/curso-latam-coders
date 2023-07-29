package com.services.curso.controller;

import static com.services.curso.constant.FileConstant.FORWARD_SLASH;
import static com.services.curso.constant.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;
import static com.services.curso.constant.FileConstant.USER_FOLDER;
import static com.services.curso.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.services.curso.domain.User;
import com.services.curso.domain.UserPrincipal;
import com.services.curso.domain.params.LoginParams;
import com.services.curso.domain.params.RecoveryPasswordParams;
import com.services.curso.domain.params.RegisterParams;
import com.services.curso.domain.params.ResetPasswordParams;
import com.services.curso.domain.params.UserParams;
import com.services.curso.domain.response.HttpResponse;
import com.services.curso.exception.domain.GenericException;
import com.services.curso.exception.domain.NotAnImageFileException;
import com.services.curso.exception.domain.UserNotFoundException;
import com.services.curso.exception.domain.UsernameExistException;
import com.services.curso.filter.JwtTokenProvider;
import com.services.curso.service.IUserService;


@RestController
@RequestMapping(path = "/user")
public class UserController {
	
	public static final String EMAIL_SENT = "An email with a new password was sent to: ";
	public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
	public static final String PASSWORD_RESET = "Contraseña restablecida correctamente";

	private AuthenticationManager authenticationManager;
	private IUserService userService;
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	public UserController(AuthenticationManager authenticationManager, IUserService userService, JwtTokenProvider jwtTokenProvider) {
	        this.authenticationManager = authenticationManager;
	        this.userService = userService;
	        this.jwtTokenProvider = jwtTokenProvider;
	 }

	    @PostMapping("/login")
	    public ResponseEntity<User> login(@RequestBody LoginParams user) throws UserNotFoundException {
	        authenticate(user.getUsername(), user.getPassword());
	        User loginUser = userService.findUserByUsername(user.getUsername());
	        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
	        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
	        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
	    }
	    
	    
	    @PostMapping("/register")
	    public ResponseEntity<User> register(@RequestBody RegisterParams request) throws UserNotFoundException, UsernameExistException, MessagingException {
	        User newUser = userService.register(request);
	        return new ResponseEntity<>(newUser, HttpStatus.OK);
	    }
	    
	    
	    @PostMapping("/new-user")
	    public ResponseEntity<User> register(@RequestBody UserParams request) throws UserNotFoundException, UsernameExistException, MessagingException, IOException, NotAnImageFileException, GenericException {
	        User newUser = userService.addNewUser(request);
	        return new ResponseEntity<>(newUser, HttpStatus.OK);
	    }
	    
	    @PostMapping("/update-user/{username}")
	    public ResponseEntity<User> register(@PathVariable(value = "username") String username, @RequestBody UserParams request) throws UserNotFoundException, UsernameExistException, MessagingException, IOException, NotAnImageFileException, GenericException {
	        User newUser = userService.updateUser(username, request);
	        return new ResponseEntity<>(newUser, HttpStatus.OK);
	    }
	   
	    @DeleteMapping("/delete/{username}")
	    @PreAuthorize("hasAnyAuthority('user:delete')")
	    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
	        userService.deleteUser(username);
	        return response(HttpStatus.OK, USER_DELETED_SUCCESSFULLY);
	    }
	   
	    @GetMapping("/find/{username}")
	    public ResponseEntity<User> getUser(@PathVariable("username") String username) throws UserNotFoundException {
	        User user = userService.findUserByUsername(username);
	        return new ResponseEntity<>(user,HttpStatus.OK);
	    }

	    @GetMapping("/list")
	    public ResponseEntity<List<User>> getAllUsers() {
	        List<User> users = userService.getUsers();
	        return new ResponseEntity<>(users, HttpStatus.OK);
	    }
	    
	    @GetMapping("/list-paginate")
	    public ResponseEntity<Page<User>> getUsersPaginate(
	    @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
		@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
	    ) {
	        Page<User> users = userService.getUsersPaginate(pageNo, pageSize);
	        return new ResponseEntity<>(users, HttpStatus.OK);
	    }
	    
		@PostMapping("/recovery-password")
		public ResponseEntity<HttpResponse> recoveryPassword(@RequestBody RecoveryPasswordParams request)
				throws MessagingException, UserNotFoundException {
			userService.recoveryPassword(request.getEmail());
			return response(HttpStatus.OK, EMAIL_SENT + request.getEmail());
		}

		@PostMapping("/reset-password")
		public ResponseEntity<HttpResponse> resetPassword(@RequestBody ResetPasswordParams request)
				throws MessagingException, UserNotFoundException, GenericException {
			userService.resetPassword(request.getPassword(), request.getEmail(), request.getToken());
			return response(HttpStatus.OK, PASSWORD_RESET);
		}
	    
	    
	    @PostMapping("/update-profile-image")
	    public ResponseEntity<User> updateProfileImage(@RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, IOException, NotAnImageFileException {
	        User user = userService.updateProfileImage(username, profileImage);
	        return new ResponseEntity<>(user, HttpStatus.OK);
	    }

	    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
	        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
	    }

	    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
	    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
	        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        try (InputStream inputStream = url.openStream()) {
	            int bytesRead;
	            byte[] chunk = new byte[1024];
	            while((bytesRead = inputStream.read(chunk)) > 0) {
	                byteArrayOutputStream.write(chunk, 0, bytesRead);
	            }
	        }
	        return byteArrayOutputStream.toByteArray();
	    }

	    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
	        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
	                message), httpStatus);
	    }

	    private HttpHeaders getJwtHeader(UserPrincipal user) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
	        return headers;
	    }

	    private void authenticate(String username, String password) {
	        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	    }

}
