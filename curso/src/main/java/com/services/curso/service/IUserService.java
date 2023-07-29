package com.services.curso.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.services.curso.domain.User;
import com.services.curso.domain.params.RegisterParams;
import com.services.curso.domain.params.UserParams;
import com.services.curso.exception.domain.GenericException;
import com.services.curso.exception.domain.NotAnImageFileException;
import com.services.curso.exception.domain.UserNotFoundException;
import com.services.curso.exception.domain.UsernameExistException;

@Service
public interface IUserService {

     User register(RegisterParams request) throws UsernameExistException;

	 List<User> getUsers();

	 Page<User> getUsersPaginate(int pageNo, int pageSize);
	 
	 User findUserByUsername(String username) throws UserNotFoundException;

	 User addNewUser(UserParams request) throws UserNotFoundException, IOException, NotAnImageFileException, GenericException, MessagingException, UsernameExistException;

	 User updateUser(String username, UserParams request) throws UserNotFoundException, UsernameExistException, IOException, NotAnImageFileException, GenericException;

	 void deleteUser(String username) throws IOException;

	 void resetPassword(String newPassword, String email, String token) throws MessagingException, UserNotFoundException, GenericException;
	    
	 void recoveryPassword(String token) throws MessagingException, UserNotFoundException;

	 User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, IOException, NotAnImageFileException;
	    
}
