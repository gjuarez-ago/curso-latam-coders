package com.services.curso.service.impl;

import static com.services.curso.constant.FileConstant.DEFAULT_USER_IMAGE_PATH;
import static com.services.curso.constant.FileConstant.DIRECTORY_CREATED;
import static com.services.curso.constant.FileConstant.DOT;
import static com.services.curso.constant.FileConstant.FILE_SAVED_IN_FILE_SYSTEM;
import static com.services.curso.constant.FileConstant.FORWARD_SLASH;
import static com.services.curso.constant.FileConstant.JPG_EXTENSION;
import static com.services.curso.constant.FileConstant.NOT_AN_IMAGE_FILE;
import static com.services.curso.constant.FileConstant.USER_FOLDER;
import static com.services.curso.constant.FileConstant.USER_IMAGE_PATH;
import static com.services.curso.constant.UserImplConstant.EMAIL_ALREADY_EXISTS;
import static com.services.curso.constant.UserImplConstant.FOUND_USER_BY_USERNAME;
import static com.services.curso.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;
import static com.services.curso.enumeration.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.services.curso.domain.User;
import com.services.curso.domain.UserPrincipal;
import com.services.curso.domain.params.RegisterParams;
import com.services.curso.domain.params.UserParams;
import com.services.curso.enumeration.Role;
import com.services.curso.exception.domain.GenericException;
import com.services.curso.exception.domain.NotAnImageFileException;
import com.services.curso.exception.domain.UserNotFoundException;
import com.services.curso.exception.domain.UsernameExistException;
import com.services.curso.repository.IUserRepository;
import com.services.curso.service.IUserService;
import com.services.curso.utils.Base64ToMultipartFile;
import com.services.curso.utils.EmailService;
import com.services.curso.utils.LoginAttemptService;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements IUserService, UserDetailsService {

	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private IUserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService;

	@Autowired
	public UserServiceImpl(IUserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
			LoginAttemptService loginAttemptService, EmailService emailService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findUserByUsername(username);

		if (user == null) {
			LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
		} else {
			validateLoginAttempt(user);
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user);
			LOGGER.info(FOUND_USER_BY_USERNAME + username);
			return userPrincipal;
		}
	}

	@Override
	public User register(RegisterParams request) throws UsernameExistException {

		validateUsername(request.getUsername());
	
		User user = new User();
		user.setNames(request.getNames());
		user.setSurnames(request.getSurnames());
		user.setUsername(request.getUsername());
		user.setJoinDate(new Date());
		user.setNumberPhone(request.getNumberPhone());
		user.setPassword(encodePassword(request.getPassword()));
		user.setActive(true);
		user.setNotLocked(true);
		user.setRole(ROLE_USER.name());
		user.setAuthorities(ROLE_USER.getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(request.getUsername()));
		
		user.setRegCreatedBy(user.getId());
		user.setRegDateCreated(new Date());
		
		userRepository.save(user);

		return user;
	}

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public Page<User> getUsersPaginate(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<User> response = userRepository.findAll(pageable);
		return response;
	}

	@Override
	public User findUserByUsername(String username) throws UserNotFoundException {
		return validateUpdateUsername(username);
	}

	@Override
	public User addNewUser(UserParams request)
			throws UserNotFoundException, IOException, NotAnImageFileException, GenericException, MessagingException, UsernameExistException {

		
		validateUsername(request.getUsername());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, 1);
		Date expireToken = calendar.getTime();

		String token = generatePassword();

		LOGGER.info("Token generate: " + token);
		

		User user = new User();

		user.setNames(request.getNames());
		user.setSurnames(request.getSurnames());
		user.setUsername(request.getUsername());
		user.setNumberPhone(request.getNumberPhone());
		user.setJoinDate(new Date());
		user.setActive(request.isActive());
		user.setNotLocked(request.isNotLocked());
		user.setRole(getRoleEnumName(request.getRole()).name());
		user.setAuthorities(getRoleEnumName(request.getRole()).getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(request.getUsername()));
		user.setRegCreatedBy(user.getId());
		user.setRegDateCreated(new Date());
		user.setPassword(encodePassword(token));
		user.setExpireToken(expireToken);
	
		if (request.getImageBase64().length() > 0) {

			MultipartFile file = Base64ToMultipartFile.base64Convert(request.getImageBase64());

			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(file.getContentType())) {
				throw new NotAnImageFileException(file.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			} else {
				System.out.println(" Si llego la imagen >>>>>>>>>>>>   =) ");
				saveProfileImage(user, file);
			}
		} else {
			throw new GenericException("No hemos encontrado la Imagen");
		}

		
//		emailService.sendNewPasswordEmail(user.getNames(), token, user.getUsername());
		userRepository.save(user);

		return user;
	}

	@Override
	public User updateUser(String username, UserParams request)
			throws UserNotFoundException, UsernameExistException, IOException, NotAnImageFileException, GenericException {

		User user = validateUpdateUsername(username);
		
		user.setNames(request.getNames());
		user.setSurnames(request.getSurnames());
		user.setJoinDate(new Date());
		user.setActive(request.isActive());
		user.setNotLocked(request.isNotLocked());
		user.setNumberPhone(request.getNumberPhone());
		user.setRole(getRoleEnumName(request.getRole()).name());
		user.setAuthorities(getRoleEnumName(request.getRole()).getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl(request.getUsername()));
		user.setRegUpdateBy(user.getId());
		user.setRegDateUpdated(new Date());

		
		if (request.getImageBase64().length() > 0) {

			MultipartFile file = Base64ToMultipartFile.base64Convert(request.getImageBase64());

			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(file.getContentType())) {
				throw new NotAnImageFileException(file.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			} else {
				System.out.println(" Si llego la imagen >>>>>>>>>>>>   =) ");
				saveProfileImage(user, file);
			}
		} else {
			throw new GenericException("No hemos encontrado la Imagen");
		}

		
		return userRepository.save(user);
	}

	@Override
	public void deleteUser(String username) throws IOException {
		User user = userRepository.findUserByUsername(username);
		Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(userFolder.toString()));
		userRepository.deleteById(user.getId());
	}

	@Override
	public void resetPassword(String newPassword, String email, String token)
			throws MessagingException, UserNotFoundException, GenericException {

		User user = userRepository.findUserByTokenAndUsername(token, email);

		if (user == null) {
			throw new GenericException("El token es incorrecto o bien ya fue utilizado.");
		}

		if (user.getExpireToken().before(new Date())) {
			throw new GenericException("El token se encuentra expirado!");
		}

		user.setToken("");
		user.setPassword(encodePassword(newPassword));
		userRepository.save(user);

	}

	@Override
	public void recoveryPassword(String email) throws MessagingException, UserNotFoundException {

		User user = findUserByUsername(email);

		if (user == null) {
			throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + email);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, 1);
		Date expireToken = calendar.getTime();

		String token = generatePassword();
		user.setToken(token);
		user.setExpireToken(expireToken);
		userRepository.save(user);

		LOGGER.info("Token generate: " + token);
		emailService.resetPassword(user.getNames(), token, user.getUsername());

	}

	@Override
	public User updateProfileImage(String username, MultipartFile profileImage)
			throws UserNotFoundException, UsernameExistException, IOException, NotAnImageFileException {
		User user = validateUpdateUsername(username);
		saveProfileImage(user, profileImage);
		return user;
	}

	private void validateLoginAttempt(User user) {
		if (user.isNotLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}

	private User validateUpdateUsername(String currentUsername) throws UserNotFoundException {

		User currentUser = userRepository.findUserByUsername(currentUsername);

		if (StringUtils.isNotBlank(currentUsername)) {
			if (currentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
			}
		}

		return currentUser;
	}

	private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
		if (profileImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(profileImage.getContentType())) {
				throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
				LOGGER.info(DIRECTORY_CREATED + userFolder);
			}
			Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
			Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION),
					REPLACE_EXISTING);
			user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
			userRepository.save(user);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}
	}

	private void validateUsername(String email) throws UsernameExistException {
		User user = userRepository.findUserByUsername(email);
		if (user != null) {
			throw new UsernameExistException(EMAIL_ALREADY_EXISTS);
		}
	}

	private String setProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
	}

	private Role getRoleEnumName(String role) {
		return Role.valueOf(role.toUpperCase());
	}

	private String getTemporaryProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username)
				.toUriString();
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

}
