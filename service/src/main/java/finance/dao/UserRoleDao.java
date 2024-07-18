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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

@Component
public class UserRoleDao extends Dao {
    private static final Logger LOGGER = Logger.getLogger( UserRoleDao.class.getName() );

    private static final String INSERT_QUERY = "INSERT INTO finance.user_role (user_id, role) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM finance.user_role WHERE user_id = ? AND role = ?;";

    public UserRoleDao() {
        super(); 
    }

    public void insertUserRole(int userId, String role) {
        getConnection(url, user, password);
        try {
            PreparedStatement statement = this.connection.prepareStatement(INSERT_QUERY);
            statement.setInt(1, userId);
            statement.setString(2, role);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }
    }

    public void deleteUserRole(int userId, String role) {
        getConnection(url, user, password);
        try {
            PreparedStatement statement = this.connection.prepareStatement(DELETE_QUERY);
            statement.setInt(1, userId);
            statement.setString(2, role);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }
    }

}
