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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import finance.authorization.AuthDao;
import finance.authorization.JwtUtils;
import finance.dao.CompanyFilingDao;
import finance.dao.CompanySummaryDao;
import finance.dao.CompanyTickerDao;
import finance.dao.DataCollectorDao;
import finance.dao.LoggedActionDao;
import finance.dao.SavedCikDao;
import finance.dao.UserCommentDao;
import finance.dao.UserRequestDao;
import finance.dao.UserRoleDao;
import finance.models.CompanyFiling;
import finance.models.CompanySummary;
import finance.models.CompanyTicker;
import finance.models.GenericFilter;
import finance.models.GenericParameters;
import finance.models.UserComment;
import finance.models.UserRequest;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GraphqlController {
    protected static final Logger LOGGER = Logger.getLogger(GraphqlController.class.getName());

    protected static final String ADMIN_ROLE = "admin";
    protected static final String MODERATOR_ROLE = "moderator";
    protected static final List<String> MANAGING_ROLES = List.of(ADMIN_ROLE,MODERATOR_ROLE);
    protected static final String COMMENTOR_ROLE = "commentor";
    
    @Autowired
    CompanyFilingDao companyFilingDao;
    @Autowired
    CompanySummaryDao companySummaryDao;
    @Autowired
    CompanyTickerDao companyTickerDao;
    @Autowired
    DataCollectorDao dataCollectorDao;
    @Autowired
    SavedCikDao savedCikDao;
    @Autowired
    UserCommentDao userCommentDao;
    @Autowired
    UserRequestDao userRequestDao;
    @Autowired
    UserRoleDao userRoleDao;
    @Autowired
    LoggedActionDao loggedActionDao;
    @Autowired
    AuthDao authDao;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    private HttpServletRequest request;
	
    // -------------------- Queries --------------------
	@QueryMapping
    public List<CompanyFiling> companyFilings(@Argument GenericParameters input){
        return companyFilingDao.getCompanyFilings(input);
    }
	
	@QueryMapping
    public List<CompanySummary> companySummaries(@Argument GenericParameters input){
        return companySummaryDao.getCompanySummaries(input);
    }

    @SchemaMapping(typeName="CompanySummary", field="tickers")
    public List<CompanyTicker> getTickers(CompanySummary companySummary) {
        GenericParameters params = new GenericParameters();
        GenericFilter filter = new GenericFilter("cik","=",companySummary.getCik());
        params.setFilters(Arrays.asList(filter));
        return companyTickerDao.getCompanyTickers(params);
    }
	
	@QueryMapping
    public List<CompanyTicker> companyTickers(@Argument GenericParameters input){
        return companyTickerDao.getCompanyTickers(input);
    }
	
	@QueryMapping
    public int timeSinceRefresh(){
        return dataCollectorDao.getTimeSinceRefresh();
    }
	
	@QueryMapping
    public List<String> savedCiks(){
        return savedCikDao.getUserSavedCiks(jwtUtils.getUserId(request.getHeader("Authorization")));
    }
	
	@QueryMapping
    public List<UserComment> userComments(@Argument GenericParameters input){
        return userCommentDao.getUserComments(input);
    }

    @SchemaMapping(typeName="UserComment", field="voteTotal")
    public int getVoteTotal(UserComment userComment) {
        return userCommentDao.getUserCommentVotes(userComment.getCommentId());
    }

    @SchemaMapping(typeName="UserComment", field="userName")
    public String getUserCommentName(UserComment userComment) {
        return authDao.getUser(userComment.getUserId()).getUsername();
    }

    @QueryMapping
    public List<UserRequest> getUserRequests(@Argument GenericParameters input) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"), MANAGING_ROLES);
        return userRequestDao.getUserRequests(input);
    }

    // -------------------- Mutations --------------------
    @MutationMapping
    public String updateUserName(@Argument String userName) {
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        authDao.updateUserName(userId, userName);
        return "User name updated";
    }

    @MutationMapping
    public String updateUserEmail(@Argument String userEmail) {
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        authDao.updateEmail(userId, userEmail);
        return "User email updated";
    }

    @MutationMapping
    public int addUserComment(@Argument String cik, @Argument float minPrice, @Argument float maxPrice, @Argument String comment) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        return userCommentDao.insertUserComment(userId, cik, minPrice, maxPrice, comment);
    }

    @MutationMapping
    public String upVoteComment(@Argument int commentId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userCommentDao.updateCommentVote(commentId, userId, 1);
        return "Comment upvoted";
    }

    @MutationMapping
    public String downVoteComment(@Argument int commentId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userCommentDao.updateCommentVote(commentId, userId, -1);
        return "Comment downvoted";
    }

    @MutationMapping
    public String deleteVoteComment(@Argument int commentId) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userCommentDao.updateCommentVote(commentId, userId, 0);
        return "Vote deleted";
    }

    @MutationMapping
    public String addToSavedCikList(@Argument String cik) {
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        savedCikDao.insertSavedCik(userId, cik);
        return "Added to watchlist";
    }

    @MutationMapping
    public String deleteFromSavedCikList(@Argument String cik) {
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        savedCikDao.deleteSavedCik(userId, cik);
        return "Deleted from watchlist";
    }

    @MutationMapping
    public String reportUserComment(@Argument int commentId, @Argument String reason) {
        jwtUtils.checkForValidRole(request.getHeader("Authorization"),COMMENTOR_ROLE);
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRequestDao.insertUserRequest(userId, commentId, "comment_report", reason);
        return "User comment reported";
    }

    @MutationMapping
    public String requestCommentorStatus(@Argument String reason) {
        int userId = jwtUtils.getUserId(request.getHeader("Authorization"));
        userRequestDao.insertUserRequest(userId, -1, "commentor_request", reason);
        return "Commentor status requested";
    }

    @MutationMapping
    public String reportDataIssue(@Argument String reason) {
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
