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
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import finance.authorization.AuthDao;
import finance.authorization.JwtUtils;
import finance.dao.CompanyFilingDao;
import finance.dao.CompanySummaryDao;
import finance.dao.CompanyTickerDao;
import finance.dao.DataCollectorDao;
import finance.dao.SavedCikDao;
import finance.dao.UserCommentDao;
import finance.models.CompanyFiling;
import finance.models.CompanySummary;
import finance.models.CompanyTicker;
import finance.models.GenericFilter;
import finance.models.GenericParameters;
import finance.models.UserComment;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class GraphqlController {
    protected static final Logger LOGGER = Logger.getLogger(GraphqlController.class.getName());
    
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
    AuthDao authDao;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    private HttpServletRequest request;
	
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
}
