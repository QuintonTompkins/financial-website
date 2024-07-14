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
package finance.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenericFilterList {
    private List<GenericFilter> filters;

    public List<GenericFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<GenericFilter> filters) {
        this.filters = filters;
    }

    public void validateGenericFilterList(List<String> stringColumnList, List<String> dateColumnList) {
        for (GenericFilter filter : this.filters) {
            filter.validateGenericFilter(stringColumnList, dateColumnList);
        }
    }

    public String generateFilterString(List<String> stringColumnList, List<String> dateColumnList) {
        validateGenericFilterList(stringColumnList, dateColumnList);
        ArrayList<String> filterStringList = new ArrayList<String>();
        for (GenericFilter filter : this.filters) {
            filterStringList.add(String.format(" %s %s ? ", filter.getField(), filter.getComparator()));
        }
        return String.join(" AND ", filterStringList);
    }

    public int setValues(PreparedStatement statement, int i, List<String> stringColumnList, List<String> dateColumnList) throws SQLException {
        for( int j = 0 ; j < filters.size() ; j++, i++){
            String field = filters.get(j).getField();
            Object value = filters.get(j).getValue();
            if(stringColumnList.contains(field) || dateColumnList.contains(field)){
                statement.setString(i, (String) value);
            }
        }
        return i;
    }

}
