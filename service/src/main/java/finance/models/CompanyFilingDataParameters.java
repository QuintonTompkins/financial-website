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

import java.util.ArrayList;
import java.util.List;

public class CompanyFilingDataParameters extends GenericParameters {
    private List<CompanyFilingDataFilter> customFilters = new ArrayList<CompanyFilingDataFilter>();

    public List<CompanyFilingDataFilter> getCustomFilters() {
        return customFilters;
    }

    public void setCustomFilters(List<CompanyFilingDataFilter> customFilters) {
        this.customFilters = customFilters;
    }

    @Override
    public String generateFilterString(List<String> stringColumnList, List<String> dateColumnList, List<String> booleanColumnList, List<String> numericColumnList) {
        validateGenericParameters(stringColumnList, dateColumnList, booleanColumnList, numericColumnList);
        ArrayList<String> filterStringList = new ArrayList<String>();
        for (GenericFilter filter : getFilters()) {
            filterStringList.add(stringifyGenericFilter(filter));
        }
        for (CompanyFilingDataFilter filter : getCustomFilters()) {
            filterStringList.add(filter.generateFilterString());
        }
        return filterStringList.size() == 0 ? "" : " WHERE " + String.join(" AND ", filterStringList);
    }

}
