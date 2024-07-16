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
import finance.models.CompanyTicker;
import finance.models.GenericParameters;

@Component
public class UserCommentDao extends Dao {
    private static final Logger LOGGER = Logger.getLogger( UserCommentDao.class.getName() );

    private static final List<String> STRING_COLUMN_LIST = Arrays.asList("comment_id", "user_id", "cik", "min_price", "max_price", "comment");
    private static final List<String> DATE_COLUMN_LIST = Arrays.asList("created");
    private static final List<String> COLUMN_LIST = Stream.of(STRING_COLUMN_LIST, DATE_COLUMN_LIST)
                                                          .flatMap(List::stream)
                                                          .collect(Collectors.toList());

    private static final String SELECT_ALL_QUERY = "SELECT "+String.join(",", COLUMN_LIST)+" FROM finance.user_comment ";
    private static final String SELECT_VOTE_COUNT_QUERY = "SELECT SUM(vote) as total_vote FROM finance.user_comment_vote WHERE comment_id = ? ;";
    private static final String QUERY_LIMIT = " LIMIT 100;";

    public UserCommentDao() {
        super(); 
    }

    public List<UserComment> getUserComments(GenericParameters params) {
        String filter = params == null ? "" : params.generateFilterString(STRING_COLUMN_LIST, DATE_COLUMN_LIST);
        String sort = params == null ? "" : params.generateSortString(STRING_COLUMN_LIST, DATE_COLUMN_LIST);
        getConnection(url, user, password);
        String sql = SELECT_ALL_QUERY +filter+sort+QUERY_LIMIT;
        List<UserComment> userComments = new ArrayList<UserComment>();
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            if(params != null){
                params.setValues(statement, 1, STRING_COLUMN_LIST, DATE_COLUMN_LIST);
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
        }

        return votes;
    }

}
