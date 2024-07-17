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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import finance.exceptions.InvalidInputException;

public class GenericSort {
    private String field;
    private Boolean ascending;
    
    public GenericSort() {
    }

    public GenericSort(String field, Boolean ascending) {
        this.field = field;
        this.ascending = ascending;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Boolean getAscending() {
        return ascending;
    }

    public void setAscending(Boolean ascending) {
        this.ascending = ascending;
    }

    public String generateSortString(List<String> stringColumnList, List<String> dateColumnList, List<String> booleanColumnList, List<String> numericColumnList) throws InvalidInputException {
        String sortString = " ";
        if(stringColumnList.contains(field) || dateColumnList.contains(field) || booleanColumnList.contains(field) || numericColumnList.contains(field)){
            sortString = String.format(" %s %s ", this.field, this.ascending ? " ASC " : " DESC ");
        }
        else{
            throw new InvalidInputException(String.format("Invalid Sort Field (Valid Values: %s)", 
                                            String.join(", ", 
                                                Stream.of(stringColumnList, dateColumnList).flatMap(List::stream).collect(Collectors.toList()))));
        }
        return sortString;
    }
}