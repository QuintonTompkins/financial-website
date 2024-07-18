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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

@Component
public class DataCollectorDao extends Dao {
    private static final Logger LOGGER = Logger.getLogger( DataCollectorDao.class.getName() );
    private static final String SELECT_QUERY = """
            SELECT 	extract(minute from ((NOW() -  MAX(end_time)))) + 
                    extract(hour from ((NOW() -  MAX(end_time)))) * 60 + 
                    extract(day from ((NOW() -  MAX(end_time)))) * 3600 AS time_change_in_minutes 
                FROM finance.data_collector;
            """;;
    
    public DataCollectorDao() {
        super(); 
    }

    public int getTimeSinceRefresh() {
        getConnection(url, user, password);
        int timeSinceRefresh = -1;
        try {
            PreparedStatement statement = this.connection.prepareStatement(SELECT_QUERY);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                timeSinceRefresh = resultSet.getInt("time_change_in_minutes");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return timeSinceRefresh;
    }

}
