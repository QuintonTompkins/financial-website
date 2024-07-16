/* 
    Finance Website
    Copyright (C) 2024  Quinton Tompkins

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package finance.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.logging.Level;
import jakarta.servlet.http.HttpServletRequest;
import finance.authorization.JwtUtils;
import finance.authorization.User;
import finance.authorization.requests.CreateRequest;
import finance.authorization.requests.LoginRequest;
import finance.authorization.requests.ResetPasswordRequest;
import finance.authorization.requests.UpdatePasswordRequest;
import finance.authorization.responses.AuthResponse;
import finance.email.EmailHandler;
import finance.authorization.AuthDao;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private static final Logger LOGGER = Logger.getLogger( AuthenticationController.class.getName() );

    private static final int TOO_MANY_REQUESTS = 10;
    private static final String BCRYPT_ENCRYPTION_ID = "bcrypt";
    private static final String TOO_MANY_REQUESTS_MESSAGE = "Too many requests";
    private static final String FAILED_LOGIN_MESSAGE = "Failed to match User/Email/Password";

    private PasswordEncoder encoder = new DelegatingPasswordEncoder(
        BCRYPT_ENCRYPTION_ID, 
        new HashMap<String, PasswordEncoder>() {{ put(BCRYPT_ENCRYPTION_ID, new BCryptPasswordEncoder()); }}
    );

    @Autowired
    AuthDao authDao;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    EmailHandler emailHandler;

    private void callFrequencyCheck(HttpServletRequest request) throws InterruptedException{
        if(authDao.getRecentAuthRequestCount(request.getRemoteAddr()) > TOO_MANY_REQUESTS){
            LOGGER.log(Level.WARNING, TOO_MANY_REQUESTS_MESSAGE);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,TOO_MANY_REQUESTS_MESSAGE);
        }
        TimeUnit.SECONDS.sleep(1);
    }

    @PostMapping("/create")
    @ResponseBody
    public AuthResponse createUser(@RequestBody CreateRequest createRequest, HttpServletRequest request) throws InterruptedException {
        callFrequencyCheck(request);
        String encryptedPassword = encoder.encode(createRequest.getPassword());
        int userId = authDao.addUser(createRequest.getUsername(), createRequest.getEmail(), encryptedPassword);
        authDao.insertAuthRequest(createRequest.getUsername(), createRequest.getEmail(), userId, request.getRemoteAddr(), "create");
        Map<String, Object> claims = new HashMap<String, Object>() {{
            put("userId", userId);
            put("ip", request.getRemoteAddr());
            put("roles", new ArrayList<String>());
        }};
        String jwt = jwtUtils.createToken(claims, createRequest.getUsername(), userId, encryptedPassword);
        AuthResponse response = new AuthResponse(jwt);
        return response;
    }

    @PostMapping("/login")
    @ResponseBody
    public AuthResponse logInUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) throws InterruptedException {
        callFrequencyCheck(request);
        User userFound = authDao.getUser(loginRequest.getUsernameEmail());
        ArrayList<String> roles = authDao.getUserRoles(userFound.getUserId());
        AuthResponse response = new AuthResponse(null);
        if(userFound.getUserId() != 0 && encoder.matches(loginRequest.getPassword(), userFound.getPassword())){
            Map<String, Object> claims = new HashMap<String, Object>() {{
                put("userId", userFound.getUserId());
                put("ip", request.getRemoteAddr());
                put("roles", roles);
            }};
            String jwt = jwtUtils.createToken(claims, userFound.getUsername(), userFound.getUserId(), userFound.getPassword());
            response = new AuthResponse(jwt);
        }
        else{
            authDao.insertAuthRequest(loginRequest.getUsernameEmail(), loginRequest.getUsernameEmail(), userFound.getUserId(), request.getRemoteAddr(), "login");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,FAILED_LOGIN_MESSAGE);
        }
        return response;
    }

    @PostMapping("/password/update")
    @ResponseBody
    public ResponseEntity<String> updateUserPassword(@RequestBody UpdatePasswordRequest passwordRequest, HttpServletRequest request) throws InterruptedException {
        callFrequencyCheck(request);
        User userFound = authDao.getUser(passwordRequest.getEmail());
        if(userFound.getUserId() != 0 && encoder.matches(passwordRequest.getPassword(), userFound.getPassword())){
            String encryptedPassword = encoder.encode(passwordRequest.getNewPassword());
            authDao.updatePassword(userFound.getUserId(), encryptedPassword);
        }
        else{
            authDao.insertAuthRequest(userFound.getUsername(), passwordRequest.getEmail(), userFound.getUserId(), request.getRemoteAddr(), "updatePassword");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,FAILED_LOGIN_MESSAGE);
        }
        return new ResponseEntity<String>("Password Updated.", HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    @ResponseBody
    public ResponseEntity<String> resetUserPassword(@RequestBody ResetPasswordRequest passwordRequest, HttpServletRequest request) throws InterruptedException {
        callFrequencyCheck(request);
        User userFound = authDao.getUser(passwordRequest.getEmail());
        authDao.insertAuthRequest(userFound.getUsername(), passwordRequest.getEmail(), userFound.getUserId(), request.getRemoteAddr(), "resetPassword");
        if(userFound.getUserId() != 0){
            String password =  RandomStringUtils.random( 15, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()-_=+?" );
            String encryptedPassword = encoder.encode(password);
            String subject = "Password Updated for Quinton's Finance Website";
            String body = "A request has been made to reset your password.\n\nYour new password is "+password+"\n\nThis email does not check responses.";
            emailHandler.sendSimpleMessage(userFound.getEmail(), subject, body);
            authDao.updatePassword(userFound.getUserId(), encryptedPassword);
        }
        return new ResponseEntity<String>("Password Reset.", HttpStatus.OK);
    }

}
