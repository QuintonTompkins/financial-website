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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import finance.dao.CompanyFilingKeyDao;
import finance.exceptions.InvalidInputException;

public class CompanyFilingDataFilter extends GenericFilter {
    
    private Boolean valueIsField;
    
    private static final List<String> VALID_NUMERIC_COMPARATORS =  Arrays.asList("=",">","<");
    

    public Boolean getValueIsField() {
        return valueIsField;
    }

    public void setValueIsField(Boolean valueIsField) {
        this.valueIsField = valueIsField;
    }

    public void validateFilter(CompanyFilingKeyDao companyFilingKeyDao) throws InvalidInputException {
        if( !VALID_NUMERIC_COMPARATORS.contains(getComparator()) ){
                throw new InvalidInputException(String.format("Invalid filter Comparator (Valid Values: %s)", 
                                                String.join(", ", VALID_NUMERIC_COMPARATORS)));
        }
        checkForFieldValidity(companyFilingKeyDao, getField());
        if(getValueIsField()){
            checkForFieldValidity(companyFilingKeyDao, (String) getValue());
        }
        else if( !(getValue() instanceof Integer) ){
            throw new InvalidInputException("Invalid company filing data key filter value provided.");
        }
    }

    public String generateFilterString() {
        if(getValueIsField()){
            return String.format(" (data->'%s'->'value')::text::numeric %s (data->'%s'->'value')::text::numeric ", getField(), getComparator(), getValue());
        }
        else{
            return String.format(" (data->'%s'->'value')::text::numeric %s %s ", getField(), getComparator(), String.valueOf(getValue()));
        }
    }

    private void checkForFieldValidity(CompanyFilingKeyDao companyFilingKeyDao, String field){
        String regex = "^[a-zA-Z0-9_-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(field);
        if (!matcher.matches()) {
            throw new InvalidInputException("Invalid company filing data key filter field provided.");
        }

        GenericParameters params = new GenericParameters();
        GenericFilter keyFilter = new GenericFilter("key","=",getField());
        params.setFilters(Arrays.asList(keyFilter));
        List<CompanyFilingKey> keys = companyFilingKeyDao.getCompanyFilingKeys(params);
        if(keys.size() != 1){
                throw new InvalidInputException("Invalid company filing data key filter field provided.");
        }
    }

}