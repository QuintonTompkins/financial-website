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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import finance.exceptions.InvalidInputException;

public class GenericFilter {

    private String field;
    private String comparator;
    private Object value;
    
    private static final List<String> VALID_NUMERIC_COMPARATORS =  Arrays.asList("=",">","<");
    private static final List<String> VALID_STRING_COMPARATORS =  Arrays.asList("=","like");
    private static final List<String> VALID_DATE_COMPARATORS =  Arrays.asList("=",">","<");
    private static final List<String> VALID_BOOLEAN_COMPARATORS =  Arrays.asList("=");

    public GenericFilter() {
    }

    public GenericFilter(String field, String comparator, Object value) {
        this.field = field;
        this.comparator = comparator;
        this.value = value;
    }

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

    public void validateGenericFilter(List<String> StringColumnList, List<String> DateColumnList, List<String> booleanColumnList, List<String> numericColumnList) throws InvalidInputException {
        if( !StringColumnList.contains(getField()) && !DateColumnList.contains(getField()) && !booleanColumnList.contains(getField()) && !numericColumnList.contains(getField())  ){
                throw new InvalidInputException(String.format("Invalid filter Column (Valid Values: %s)", 
                                                String.join(", ", 
                                                    Stream.of(StringColumnList, DateColumnList, booleanColumnList, numericColumnList)
                                                            .flatMap(List::stream).collect(Collectors.toList()))));
        }
        if( (StringColumnList.contains(getField()) && !VALID_STRING_COMPARATORS.contains(getComparator())) ){
                throw new InvalidInputException(String.format("Invalid filter Comparator (Valid Values: %s)", 
                                                String.join(", ", VALID_STRING_COMPARATORS)));
        }
        if( (DateColumnList.contains(getField()) && !VALID_DATE_COMPARATORS.contains(getComparator())) ){
                throw new InvalidInputException(String.format("Invalid filter Comparator (Valid Values: %s)", 
                                                String.join(", ", VALID_DATE_COMPARATORS)));
        }
        if( (booleanColumnList.contains(getField()) && !VALID_BOOLEAN_COMPARATORS.contains(getComparator())) ){
                throw new InvalidInputException(String.format("Invalid filter Comparator (Valid Values: %s)", 
                                                String.join(", ", VALID_BOOLEAN_COMPARATORS)));
        }
        if( (numericColumnList.contains(getField()) && !VALID_NUMERIC_COMPARATORS.contains(getComparator())) ){
                throw new InvalidInputException(String.format("Invalid filter Comparator (Valid Values: %s)", 
                                                String.join(", ", VALID_NUMERIC_COMPARATORS)));
        }
    }

}