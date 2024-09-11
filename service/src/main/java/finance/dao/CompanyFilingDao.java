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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import finance.models.CompanyFiling;
import finance.models.CompanyFilingDataParameters;


@Component
public class CompanyFilingDao extends Dao{
    private static final Logger LOGGER = Logger.getLogger( CompanyFilingDao.class.getName() );
    
    protected static final List<String> STRING_COLUMN_LIST = Arrays.asList("cik", "accession_number", "form");
    protected static final List<String> DATE_COLUMN_LIST = Arrays.asList("filing_date", "report_date");
    protected static final List<String> NO_FILTER_COLUMN_LIST = Arrays.asList("data");
    protected static final List<String> COLUMN_LIST = Stream.of(STRING_COLUMN_LIST, DATE_COLUMN_LIST, NO_FILTER_COLUMN_LIST)
                                                          .flatMap(List::stream)
                                                          .collect(Collectors.toList());

    private static final String SELECT_ALL_QUERY = "SELECT "+String.join(",", COLUMN_LIST)+" FROM finance.recent_company_filings_view ";
    private static final String QUERY_LIMIT = " LIMIT 50;";

    public CompanyFilingDao() {
        super(); 
    }

    public List<CompanyFiling> getCompanyFilings(CompanyFilingDataParameters params) {
        String filter = params == null ? "" : params.generateFilterString(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
        String sort = params == null ? "" : params.generateSortString(STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
        getConnection(url, user, password, 0);
        String sql = SELECT_ALL_QUERY +filter+sort+QUERY_LIMIT;
        List<CompanyFiling> companyFilings = new ArrayList<CompanyFiling>();
        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            if(params != null){
                params.setValues(statement, 1, STRING_COLUMN_LIST, DATE_COLUMN_LIST, BOOLEAN_COLUMN_LIST, NUMERIC_COLUMN_LIST);
            }
            ResultSet resultSet = statement.executeQuery();

            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = mapper.getFactory();
            while (resultSet.next()) {
                companyFilings.add(new CompanyFiling(
                    resultSet.getString("cik"),
                    resultSet.getString("accession_number"),
                    resultSet.getString("filing_date"),
                    resultSet.getString("report_date"),
                    resultSet.getString("form"),
                    mapper.readTree(factory.createParser(resultSet.getString("data")))
                ));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally{
            closeConnection();
        }

        return companyFilings;
    }
}
