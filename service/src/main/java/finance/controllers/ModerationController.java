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

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import finance.authorization.JwtUtils;
import finance.dao.LoggedActionDao;
import finance.dao.UserCommentDao;
import finance.dao.UserRequestDao;
import finance.dao.UserRoleDao;
import finance.exceptions.InvalidInputException;
import finance.models.GenericParameters;
import finance.models.UserRequest;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ModerationController {
    protected static final Logger LOGGER = Logger.getLogger(ModerationController.class.getName());

    protected static final String ADMIN_ROLE = "admin";
    protected static final String MODERATOR_ROLE = "moderator";
    protected static final List<String> MANAGING_ROLES = List.of(ADMIN_ROLE,MODERATOR_ROLE);
    protected static final String COMMENTOR_ROLE = "commentor";
    
    private static final String REASON_REGEX = "^[a-zA-Z0-9. ]*$";
    private static final Pattern REASON_PATTERN = Pattern.compile(REASON_REGEX);
    
    @Autowired
    UserCommentDao userCommentDao;
    @Autowired
    UserRequestDao userRequestDao;
    @Autowired
    UserRoleDao userRoleDao;
    @Autowired
    LoggedActionDao loggedActionDao;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    private HttpServletRequest request;
	
    // -------------------- Queries --------------------
    @QueryMapping
    public List<UserRequest> getUserRequests(@Argument GenericParameters input) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"), MANAGING_ROLES);
        return userRequestDao.getUserRequests(input);
    }

    // -------------------- Mutations --------------------

    @MutationMapping
    public String reportUserComment(@Argument int commentId, @Argument String reason) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        Matcher reasonMatcher = REASON_PATTERN.matcher(reason);
        if (!reasonMatcher.matches()) {
            throw new InvalidInputException("Invalid comment provided.");
        }
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRequestDao.insertUserRequest(userId, commentId, "REASON_report", reason);
        return "User comment reported";
    }

    @MutationMapping
    public String requestCommentorStatus(@Argument String reason) {
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        Matcher reasonMatcher = REASON_PATTERN.matcher(reason);
        if (!reasonMatcher.matches()) {
            throw new InvalidInputException("Invalid comment provided.");
        }
        userRequestDao.insertUserRequest(userId, -1, "commentor_request", reason);
        return "Commentor status requested";
    }

    @MutationMapping
    public String reportDataIssue(@Argument String reason) {
        Matcher reasonMatcher = REASON_PATTERN.matcher(reason);
        if (!reasonMatcher.matches()) {
            throw new InvalidInputException("Invalid comment provided.");
        }
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRequestDao.insertUserRequest(userId, -1, "data_issue", reason);
        return "Data issue reported";
    }

    @MutationMapping
    public String hideUserComment(@Argument int commentId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),MANAGING_ROLES);
        int managingUserId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userCommentDao.hideComment(commentId);
        loggedActionDao.insertLoggedAction(managingUserId,commentId, "removed user comment");
        return "User comment removed";
    }

    @MutationMapping
    public String markUserRequestCompleted(@Argument int requestId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),MANAGING_ROLES);
        int managingUserId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRequestDao.completeRequest(requestId);
        loggedActionDao.insertLoggedAction(managingUserId,requestId, "marked user request completed");
        return "User request marked as completed";
    }

    @MutationMapping
    public String grantUserCommentorRole(@Argument int userId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),MANAGING_ROLES);
        int managingUserId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRoleDao.insertUserRole(userId, COMMENTOR_ROLE);
        loggedActionDao.insertLoggedAction(managingUserId,userId, "gave commentor role");
        return "User given commentor role";
    }

    @MutationMapping
    public String revokeUserCommentorRole(@Argument int userId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),MANAGING_ROLES);
        int managingUserId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRoleDao.deleteUserRole(userId, COMMENTOR_ROLE);
        loggedActionDao.insertLoggedAction(managingUserId,userId, "revoked user commentor role");
        return "User commentor role removed";
    }

    @MutationMapping
    public String grantUserModeratorRole(@Argument int userId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),ADMIN_ROLE);
        int managingUserId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRoleDao.insertUserRole(userId, MODERATOR_ROLE);
        loggedActionDao.insertLoggedAction(managingUserId,userId, "gave moderator role");
        return "User given moderator role";
    }

    @MutationMapping
    public String revokeUserModeratorRole(@Argument int userId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),ADMIN_ROLE);
        int managingUserId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRoleDao.deleteUserRole(userId, MODERATOR_ROLE);
        loggedActionDao.insertLoggedAction(managingUserId,userId, "revoked user moderator role");
        return "User moderator role removed";
    }
    
}
