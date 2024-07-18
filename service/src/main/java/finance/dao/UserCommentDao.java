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
package finance.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import finance.models.UserComment;
import finance.models.GenericFilter;
import finance.models.GenericParameters;

@Component
public class UserCommentDao extends Dao {
    private static final Logger LOGGER = Logger.getLogger( UserCommentDao.class.getName() );

    private static final List<String> STRING_COLUMN_LIST = Arrays.asList("cik", "comment");
    private static final List<String> DATE_COLUMN_LIST = Arrays.asList("created");
    private static final List<String> BOOLEAN_COLUMN_LIST = Arrays.asList("hidden");
    private static final List<String> NUMERIC_COLUMN_LIST = Arrays.asList("comment_id", "user_id", "min_price", "max_price");
    private static final List<String> COLUMN_LIST = Stream.of(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST)
                                                          .flatMap(List::stream)
                                                          .collect(Collectors.toList());

    private static final String SELECT_ALL_QUERY = "SELECT "+String.join(",", COLUMN_LIST)+" FROM finance.user_comment";
    private static final String SELECT_VOTE_COUNT_QUERY = "SELECT SUM(vote) as total_vote FROM finance.user_comment_vote WHERE comment_id = ? ;";
    private static final String INSERT_COMMENT_QUERY = "INSERT INTO finance.user_comment (user_id, cik, min_price, max_price, comment) VALUES (?, ?, ?, ?, ?)";
    private static final String HIDE_COMMENT_QUERY = "UPDATE finance.user_comment SET hidden = true WHERE comment_id = ?";
    private static final String UPDATE_USER_VOTE = "INSERT INTO finance.user_comment_vote (  comment_id, user_id, vote ) VALUES (?,?,?)" +
                                                        " ON CONFLICT ( comment_id , user_id ) DO UPDATE SET vote = ? ";
    private static final String QUERY_LIMIT = " LIMIT 50;";

    public UserCommentDao() {
        super(); 
    }

    public List<UserComment> getUserComments(GenericParameters params) {
        if(params == null){
            params = new GenericParameters();
        }
        params.getFilters().add(new GenericFilter("hidden","=",false));
        String filter = params.generateFilterString(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
        String sort = params.generateSortString(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
        getConnection(url, user, password);
        String sql = SELECT_ALL_QUERY +filter+sort+QUERY_LIMIT;
        List<UserComment> userComments = new ArrayList<UserComment>();
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            if(params != null){
                params.setValues(statement, 1, STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
            }
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userComments.add(new UserComment(
                    resultSet.getInt("comment_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("cik"),
                    (LocalDateTime) resultSet.getObject("created"),
                    resultSet.getFloat("min_price"),
                    resultSet.getFloat("max_price"),
                    resultSet.getString("comment"),
                    null,
                    null
                ));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return userComments;
    }

    public int getUserCommentVotes(int commentId) {
        getConnection(url, user, password);
        int votes = 0;
        try {
            PreparedStatement statement = this.connection.prepareStatement(SELECT_VOTE_COUNT_QUERY);
            statement.setInt(1, commentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                votes = resultSet.getInt("total_vote");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return votes;
    }

    public int insertUserComment(int userId, String cik, float minPrice, float maxPrice, String comment) {
        getConnection(url, user, password);
        int commentId = -1;
        try {
            PreparedStatement statement = this.connection.prepareStatement(INSERT_COMMENT_QUERY,Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setString(2, cik);
            statement.setFloat(3, minPrice);
            statement.setFloat(4, maxPrice);
            statement.setString(5, comment);
            ResultSet resultSet = statement.getGeneratedKeys();

            while (resultSet.next()) {
                commentId = resultSet.getInt("comment_id");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return commentId;
    }

    public void updateCommentVote(int commentId, int userId, int vote) {
        getConnection(url, user, password);
        try {
            PreparedStatement statement = this.connection.prepareStatement(UPDATE_USER_VOTE);
            statement.setInt(1, commentId);
            statement.setInt(2, userId);
            statement.setInt(3, vote);
            statement.setInt(4, vote);
            statement.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }
    }

    public void hideComment(int commentId) {
        getConnection(url, user, password);
        try {
            PreparedStatement statement = this.connection.prepareStatement(HIDE_COMMENT_QUERY);
            statement.setInt(1, commentId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }
    }

}
