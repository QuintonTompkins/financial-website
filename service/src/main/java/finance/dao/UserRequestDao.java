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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import finance.models.UserRequest;
import finance.models.GenericParameters;

@Component
public class UserRequestDao extends Dao {
    private static final Logger LOGGER = Logger.getLogger( UserRequestDao.class.getName() );

    private static final List<String> NUMERIC_COLUMN_LIST = Arrays.asList("request_id", "target_id", "user_id");
    private static final List<String> STRING_COLUMN_LIST = Arrays.asList("type", "reason");
    private static final List<String> DATE_COLUMN_LIST = Arrays.asList("created");
    private static final List<String> BOOLEAN_COLUMN_LIST = Arrays.asList("complete");
    private static final List<String> COLUMN_LIST = Stream.of(NUMERIC_COLUMN_LIST, STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST)
                                                          .flatMap(List::stream)
                                                          .collect(Collectors.toList());

    private static final String SELECT_ALL_QUERY = "SELECT "+String.join(",", COLUMN_LIST)+" FROM finance.user_request ";
    private static final String QUERY_LIMIT = " LIMIT 50;";
    
    private static final String INSERT_QUERY = "INSERT INTO finance.user_request (user_id, target_id, type, reason) VALUES (?, ?, ?, ?)";
    private static final String COMPLETE_REQUEST_QUERY = "UPDATE finance.user_request SET complete = true WHERE request_id = ?";

    public UserRequestDao() {
        super(); 
    }

    public List<UserRequest> getUserRequests(GenericParameters params) {
        String filter = params == null ? "" : params.generateFilterString(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
        String sort = params == null ? "" : params.generateSortString(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
        getConnection(url, user, password);
        String sql = SELECT_ALL_QUERY +filter+sort+QUERY_LIMIT;
        List<UserRequest> userRequests = new ArrayList<UserRequest>();
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            if(params != null){
                params.setValues(statement, 1, STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
            }
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userRequests.add(new UserRequest(
                    resultSet.getInt("request_id"),
                    resultSet.getInt("comment_id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("type"),
                    resultSet.getString("reason"),
                    resultSet.getString("created"),
                    resultSet.getBoolean("handled")
                ));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return userRequests;
    }

    public void insertUserRequest(int userId, int commentId, String type, String reason) {
        getConnection(url, user, password);
        try {
            PreparedStatement statement = this.connection.prepareStatement(INSERT_QUERY);
            statement.setInt(1, userId);
            statement.setInt(2, commentId);
            statement.setString(3, type);
            statement.setString(4, reason);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }
    }

    public void completeRequest(int requestId) {
        getConnection(url, user, password);
        try {
            PreparedStatement statement = this.connection.prepareStatement(COMPLETE_REQUEST_QUERY);
            statement.setInt(1, requestId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }
    }

}
