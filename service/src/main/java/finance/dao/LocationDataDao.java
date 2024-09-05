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

import org.springframework.stereotype.Component;

import finance.models.LocationData;
import finance.models.SicDetails;


@Component
public class LocationDataDao extends Dao {
    private static final Logger LOGGER = Logger.getLogger( LocationDataDao.class.getName() );

    private static final String SELECT_ALL_LOCATION_DATA_QUERY = "SELECT state_country, SUM(total_recently_active) as total_recently_active FROM finance.location_data GROUP BY state_country";

    private static final String SELECT_ALL_SIC_DETAILS_QUERY = "SELECT sic_description, total_recently_active FROM finance.location_data WHERE state_country = ? ORDER BY total_recently_active DESC";
    
    public LocationDataDao() {
        super(); 
    }

    public List<LocationData> getLocationData() {
        getConnection(url, user, password);
        List<LocationData> locationDatas = new ArrayList<LocationData>();
        try {
            PreparedStatement statement = this.connection.prepareStatement(SELECT_ALL_LOCATION_DATA_QUERY);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                locationDatas.add(new LocationData(
                    resultSet.getString("state_country"),
                    resultSet.getInt("total_recently_active"),
                    null
                ));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return locationDatas;
    }

    public List<SicDetails> getSicDetails(String stateCountry) {
        getConnection(url, user, password);
        List<SicDetails> sicDetails = new ArrayList<SicDetails>();
        try {
            PreparedStatement statement = this.connection.prepareStatement(SELECT_ALL_SIC_DETAILS_QUERY);
            statement.setString(1, stateCountry);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                sicDetails.add(new SicDetails(
                    resultSet.getString("sic_description"),
                    resultSet.getInt("total_recently_active")
                ));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return sicDetails;
    }

}
