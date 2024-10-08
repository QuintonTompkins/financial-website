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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

@Component
public class SavedCikDao extends Dao {
    private static final Logger LOGGER = Logger.getLogger( SavedCikDao.class.getName() );
    private static final String SELECT_QUERY = "SELECT cik FROM finance.user_saved_cik WHERE user_id = ?;";
    private static final String INSERT_QUERY = "INSERT INTO finance.user_saved_cik (cik, user_id) VALUES (?,?) ;";
    private static final String DELETE_QUERY = "DELETE FROM finance.user_saved_cik WHERE cik = ? AND user_id = ?;";
    
    public SavedCikDao() {
        super(); 
    }

    public List<String> getUserSavedCiks(int userId) {
        getConnection(url, user, password, 0);
        List<String> savedCiks = new ArrayList<String>();
        try {
            PreparedStatement statement = this.connection.prepareStatement(SELECT_QUERY);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                savedCiks.add(resultSet.getString("cik"));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return savedCiks;
    }

    public void insertSavedCik(int userId, String cik) {
        getConnection(url, user, password, 0);
        try {
            PreparedStatement statement = this.connection.prepareStatement(INSERT_QUERY);
            statement.setString(1, cik);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }
    }

    public void deleteSavedCik(int userId, String cik) {
        getConnection(url, user, password, 0);
        try {
            PreparedStatement statement = this.connection.prepareStatement(DELETE_QUERY);
            statement.setString(1, cik);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }
    }

}
