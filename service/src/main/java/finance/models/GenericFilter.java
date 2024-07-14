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

public class GenericFilter {

    private String field;
    private String comparator;
    private Object value;
    
    private static final List<String> VALID_STRING_COMPARATORS =  Arrays.asList("=","like");
    private static final List<String> VALID_DATE_COMPARATORS =  Arrays.asList("=",">","<");

    public GenericFilter() {
    }

    public GenericFilter(String field, String comparator, List<String> value) {
        this.field = field;
        this.comparator = comparator;
        this.value = value;
    }

    // Getters and Setters
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void validateGenericFilter(List<String> StringColumnList, List<String> DateColumnList) throws RuntimeException{
        if( !StringColumnList.contains(getField()) && !DateColumnList.contains(getField())  ){
                throw new RuntimeException("Invalid filter Column");
        }
        if( (StringColumnList.contains(getField()) && !VALID_STRING_COMPARATORS.contains(getComparator())) ||
            (DateColumnList.contains(getField()) && !VALID_DATE_COMPARATORS.contains(getComparator()))  ){
                throw new RuntimeException("Invalid filter Comparator");
        }
    }

}