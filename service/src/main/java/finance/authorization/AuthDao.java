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
package finance.authorization;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import finance.dao.Dao;

@Component
public class AuthDao extends Dao{
    private static final Logger LOGGER = Logger.getLogger( AuthDao.class.getName() );

    public AuthDao() {
        super(); 
    }

    public int addUser(String userName,
                        String userEmail,
                        String encryptedPassword){
        getConnection(url, user, password);
        String sql = "insert into finance.user (user_name, user_email, user_password) values (?,?,?)";
        int userId = -1;
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,userName);
            statement.setString(2,userEmail);
            statement.setString(3,encryptedPassword);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        return userId;
    }

    public User getUser(int userId){
        getConnection(url, user, password);
        String sql;
        sql = "SELECT user_id, user_name, user_email, user_password FROM finance.user WHERE user_id = ?";
        User user = new User();
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setInt(1,userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                user = new User(resultSet.getString("user_name"),
                                resultSet.getString("user_password"),
                                resultSet.getString("user_email"),
                                resultSet.getInt("user_id"));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        return user;
    }

    public User getUser(String usernameEmail){
        getConnection(url, user, password);
        String sql;
        sql = "SELECT user_id, user_name, user_email, user_password FROM finance.user WHERE user_name = ? OR user_email = ?";
        User user = new User();
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1,usernameEmail);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                user = new User(resultSet.getString("user_name"),
                                resultSet.getString("user_password"),
                                resultSet.getString("user_email"),
                                resultSet.getInt("user_id"));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        return user;
    }

    public void insertAuthRequest(String userName, String userEmail, int userId, String origin, String authType){
        getConnection(url, user, password);
        String sql = "insert into wargame.auth_request (user_name, user_email, player_id_found, origin, auth_type) values (?,?,?,?,?)";
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,userName);
            statement.setString(2,userEmail);
            statement.setInt(3,userId);
            statement.setString(4,origin);
            statement.setString(5,authType);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to record auth request", ex);
            throw new RuntimeException(ex);
        }
    }

    public int getRecentAuthRequestCount(String origin){
        getConnection(url, user, password);
        String sql = "SELECT count(*) FROM finance.auth_request WHERE origin = ? AND date_attempted > now() - INTERVAL '1 HOURS'";
        int requestCount = 99;
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1,origin);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                requestCount = resultSet.getInt("count");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to record auth attempt", ex);
            throw new RuntimeException(ex);
        }

        return requestCount;
    }

    public void updatePassword(int userId, String newPassword){
        getConnection(url, user, password);
        String sql = "UPDATE finance.user SET user_password = ? WHERE user_id = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1,newPassword);
            statement.setInt(2,userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update password", ex);
            throw new RuntimeException(ex);
        }
    }

}
