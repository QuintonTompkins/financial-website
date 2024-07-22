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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import finance.authorization.JwtUtils;
import finance.authorization.User;
import finance.dao.AuthDao;
import finance.email.EmailHandler;
import finance.exceptions.InvalidInputException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    protected static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    private static final int TOO_MANY_REQUESTS = 10;
    private static final String BCRYPT_ENCRYPTION_ID = "bcrypt";
    private static final String TOO_MANY_REQUESTS_MESSAGE = "Too many requests";
    private static final String FAILED_LOGIN_MESSAGE = "Failed to match User/Email/Password";

    
    private PasswordEncoder encoder = new DelegatingPasswordEncoder(
        BCRYPT_ENCRYPTION_ID, 
        new HashMap<String, PasswordEncoder>() {{ put(BCRYPT_ENCRYPTION_ID, new BCryptPasswordEncoder()); }}
    );

    protected static final String ADMIN_ROLE = "admin";
    protected static final String MODERATOR_ROLE = "moderator";
    protected static final List<String> MANAGING_ROLES = List.of(ADMIN_ROLE,MODERATOR_ROLE);
    protected static final String COMMENTOR_ROLE = "commentor";

    private static final String EMAIL_REGEX = "^(.+)@(\\\\S+)$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9]*$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
    
    @Autowired
    AuthDao authDao;

    @Autowired
    EmailHandler emailHandler;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    private HttpServletRequest request;

    private void callFrequencyCheck(HttpServletRequest request) throws InterruptedException {
        if(authDao.getRecentAuthRequestCount(request.getRemoteAddr()) > TOO_MANY_REQUESTS){
            LOGGER.log(Level.WARNING, TOO_MANY_REQUESTS_MESSAGE);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,TOO_MANY_REQUESTS_MESSAGE);
        }
        TimeUnit.SECONDS.sleep(1);
    }
	
    // -------------------- Queries --------------------
	
    
    // -------------------- Mutations --------------------
    @MutationMapping
    public String login(@Argument String userNameEmail, @Argument String password) throws InterruptedException {
        callFrequencyCheck(request);
        User userFound = authDao.getUser(userNameEmail);
        ArrayList<String> roles = authDao.getUserRoles(userFound.getUserId());
        String jwt = "";
        if(userFound.getUserId() != 0 && encoder.matches(password, userFound.getPassword())){
            Map<String, Object> claims = new HashMap<String, Object>() {{
                put("userId", userFound.getUserId());
                put("ip", request.getRemoteAddr());
                put("roles", roles);
            }};
            jwt = jwtUtils.createToken(claims, userFound.getUsername(), userFound.getUserId(), userFound.getPassword());
        }
        else{
            authDao.insertAuthRequest(userNameEmail, userNameEmail, userFound.getUserId(), request.getRemoteAddr(), "login");
            throw new AuthorizationServiceException(FAILED_LOGIN_MESSAGE);
        }
        return jwt;
    }

    @MutationMapping
    public String createUser(@Argument String userName, @Argument String email, @Argument String password) throws InterruptedException {
        callFrequencyCheck(request);
        Matcher emailMathcer = EMAIL_PATTERN.matcher(email);
        Matcher usernameMatcher = USERNAME_PATTERN.matcher(userName);
        if (!emailMathcer.matches() || !usernameMatcher.matches() || userName.length() > 20) {
            throw new InvalidInputException("Invalid email/username provided.");
        }
        String encryptedPassword = encoder.encode(password);
        int userId = authDao.addUser(userName, email, encryptedPassword);
        authDao.insertAuthRequest(userName, email, userId, request.getRemoteAddr(), "create");
        Map<String, Object> claims = new HashMap<String, Object>() {{
            put("userId", userId);
            put("ip", request.getRemoteAddr());
            put("roles", new ArrayList<String>());
        }};
        String jwt = jwtUtils.createToken(claims, userName, userId, encryptedPassword);
        return jwt;
    }

    @MutationMapping
    public String resetPassword(@Argument String email) throws InterruptedException {
        callFrequencyCheck(request);
        User userFound = authDao.getUser(email);
        authDao.insertAuthRequest(userFound.getUsername(), email, userFound.getUserId(), request.getRemoteAddr(), "resetPassword");
        if(userFound.getUserId() != 0){
            String password =  RandomStringUtils.random( 15, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()-_=+?" );
            String encryptedPassword = encoder.encode(password);
            String subject = "Password Updated for Quinton's Finance Website";
            String body = "A request has been made to reset your password.\n\nYour new password is "+password+"\n\nThis email does not check responses.";
            emailHandler.sendSimpleMessage(userFound.getEmail(), subject, body);
            authDao.updatePassword(userFound.getUserId(), encryptedPassword);
        }
        return "Password Reset.";
    }

    @MutationMapping
    public String updatePassword(@Argument String email, @Argument String oldPassword, @Argument String newPassword) throws InterruptedException {
        callFrequencyCheck(request);
        User userFound = authDao.getUser(email);
        if(userFound.getUserId() != 0 && encoder.matches(oldPassword, userFound.getPassword())){
            String encryptedPassword = encoder.encode(newPassword);
            authDao.updatePassword(userFound.getUserId(), encryptedPassword);
        }
        else{
            authDao.insertAuthRequest(userFound.getUsername(), email, userFound.getUserId(), request.getRemoteAddr(), "updatePassword");
            throw new AuthorizationServiceException(FAILED_LOGIN_MESSAGE);
        }
        return "Password Updated.";
    }

    @MutationMapping
    public String updateUserName(@Argument String userName) {
        Claims claims = jwtUtils.getClaims(request.getHeader("Authorization"));
        Matcher usernameMatcher = USERNAME_PATTERN.matcher(userName);
        if (!usernameMatcher.matches() || userName.length() > 20) {
            throw new InvalidInputException("Invalid username provided.");
        }
        int userId = (int) claims.get("userId");
        authDao.updateUserName(userId, userName);
        Map<String, Object> newClaims = new HashMap<String, Object>() {{
            put("userId", userId);
            put("ip", claims.get("ip"));
            put("roles", claims.get("roles"));
        }};
        User userFound = authDao.getUser(userId);
        String jwt = jwtUtils.createToken(newClaims, userName, userId, userFound.getPassword());
        return jwt;
    }

    @MutationMapping
    public String updateEmail(@Argument String email) {
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            throw new InvalidInputException("Invalid username provided.");
        }
        authDao.updateEmail(userId, email);
        return "User email updated";
    }
    
}
