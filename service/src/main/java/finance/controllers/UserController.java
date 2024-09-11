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
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import finance.authorization.JwtUtils;
import finance.dao.AuthDao;
import finance.dao.SavedCikDao;
import finance.dao.UserCommentDao;
import finance.dao.UserRoleDao;
import finance.exceptions.InvalidInputException;
import finance.models.GenericParameters;
import finance.models.UserComment;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    protected static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    protected static final String COMMENTOR_ROLE = "commentor";
    
    private static final String CIK_REGEX = "^[0-9]*$";
    private static final Pattern CIK_PATTERN = Pattern.compile(CIK_REGEX);

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    private HttpServletRequest request;
	
    // -------------------- Queries --------------------
	@QueryMapping
    public List<String> savedCiks(){
        SavedCikDao savedCikDao = new SavedCikDao();
        return savedCikDao.getUserSavedCiks(jwtUtils.getUserId(request.getHeader("Authorization")));
    }
	
	@QueryMapping
    public List<UserComment> userComments(@Argument GenericParameters input){
        UserCommentDao userCommentDao = new UserCommentDao();
        return userCommentDao.getUserComments(input);
    }

    @SchemaMapping(typeName="UserComment", field="voteTotal")
    public int getVoteTotal(UserComment userComment) {
        UserCommentDao userCommentDao = new UserCommentDao();
        return userCommentDao.getUserCommentVotes(userComment.getCommentId());
    }

    @SchemaMapping(typeName="UserComment", field="userName")
    public String getUserCommentName(UserComment userComment) {
        AuthDao authDao = new AuthDao();
        return authDao.getUser(userComment.getUserId()).getUsername();
    }

    // -------------------- Mutations --------------------
    @MutationMapping
    public int addUserComment(@Argument String cik, @Argument float minPrice, @Argument float maxPrice, @Argument String comment) {
        UserCommentDao userCommentDao = new UserCommentDao();
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        return userCommentDao.insertUserComment(userId, cik, minPrice, maxPrice, comment);
    }

    @MutationMapping
    public String upVoteComment(@Argument int commentId) {
        UserCommentDao userCommentDao = new UserCommentDao();
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userCommentDao.updateCommentVote(commentId, userId, 1);
        return "Comment upvoted";
    }

    @MutationMapping
    public String downVoteComment(@Argument int commentId) {
        UserCommentDao userCommentDao = new UserCommentDao();
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userCommentDao.updateCommentVote(commentId, userId, -1);
        return "Comment downvoted";
    }

    @MutationMapping
    public String deleteVoteComment(@Argument int commentId) {
        UserCommentDao userCommentDao = new UserCommentDao();
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userCommentDao.updateCommentVote(commentId, userId, 0);
        return "Vote deleted";
    }

    @MutationMapping
    public String addToSavedCikList(@Argument String cik) {
        SavedCikDao savedCikDao = new SavedCikDao();
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        Matcher cikMatcher = CIK_PATTERN.matcher(cik);
        if (!cikMatcher.matches()) {
            throw new InvalidInputException("Invalid cik provided.");
        }
        savedCikDao.insertSavedCik(userId, cik);
        return "Added to watchlist";
    }

    @MutationMapping
    public String deleteFromSavedCikList(@Argument String cik) {
        SavedCikDao savedCikDao = new SavedCikDao();
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        Matcher cikMatcher = CIK_PATTERN.matcher(cik);
        if (!cikMatcher.matches()) {
            throw new InvalidInputException("Invalid cik provided.");
        }
        savedCikDao.deleteSavedCik(userId, cik);
        return "Deleted from watchlist";
    }
    
}
