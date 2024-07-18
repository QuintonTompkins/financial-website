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

import finance.models.CompanySummary;
import finance.models.GenericParameters;

@Component
public class CompanySummaryDao extends Dao {
    private static final Logger LOGGER = Logger.getLogger( CompanySummaryDao.class.getName() );

    protected static final List<String> STRING_COLUMN_LIST = Arrays.asList("cik", "name", "sic_description", "category", "entity_type", "street1", "street2", "city", "state_country", "zip_code", "state_country_description");

    private static final String SELECT_ALL_QUERY = "SELECT "+String.join(",", STRING_COLUMN_LIST)+" FROM finance.company_summary ";
    private static final String QUERY_LIMIT = " LIMIT 50;";

    public CompanySummaryDao() {
        super(); 
    }

    public List<CompanySummary> getCompanySummaries(GenericParameters params) {
        String filter = params == null ? "" : params.generateFilterString(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
        String sort = params == null ? "" : params.generateSortString(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
        getConnection(url, user, password);
        String sql = SELECT_ALL_QUERY +filter+sort+QUERY_LIMIT;
        List<CompanySummary> companySummaries = new ArrayList<CompanySummary>();
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            if(params != null){
                params.setValues(statement, 1, STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
            }
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                companySummaries.add(new CompanySummary(
                    resultSet.getString("cik"),
                    resultSet.getString("name"),
                    resultSet.getString("sic_description"),
                    resultSet.getString("category"),
                    resultSet.getString("entity_type"),
                    resultSet.getString("street1"),
                    resultSet.getString("street2"),
                    resultSet.getString("city"),
                    resultSet.getString("state_country"),
                    resultSet.getString("zip_code"),
                    resultSet.getString("state_country_description"),
                    null
                ));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return companySummaries;
    }

}
