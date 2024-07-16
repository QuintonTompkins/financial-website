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

public class CompanySummary {

    private String cik;
    private String name;
    private String sicDescription;
    private String category;
    private String entityType;
    private String street1;
    private String street2;
    private String city;
    private String stateCountry;
    private String zipCode;
    private String stateCountryDescription;
    private List<CompanyTicker> tickers;

    public CompanySummary(String cik, String name, String sicDescription, String category, String entityType,
                          String street1, String street2, String city, String stateCountry, String zipCode,
                          String stateCountryDescription, List<CompanyTicker> tickers) {
        this.cik = cik;
        this.name = name;
        this.sicDescription = sicDescription;
        this.category = category;
        this.entityType = entityType;
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.stateCountry = stateCountry;
        this.zipCode = zipCode;
        this.stateCountryDescription = stateCountryDescription;
        this.tickers = tickers;
    }

    public String getCik() {
        return cik;
    }

    public String getName() {
        return name;
    }

    public String getSicDescription() {
        return sicDescription;
    }

    public String getCategory() {
        return category;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getCity() {
        return city;
    }

    public String getStateCountry() {
        return stateCountry;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getStateCountryDescription() {
        return stateCountryDescription;
    }

    public List<CompanyTicker> getTickers() {
        return tickers;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSicDescription(String sicDescription) {
        this.sicDescription = sicDescription;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStateCountry(String stateCountry) {
        this.stateCountry = stateCountry;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setStateCountryDescription(String stateCountryDescription) {
        this.stateCountryDescription = stateCountryDescription;
    }

    public void setTickers(List<CompanyTicker> tickers) {
        this.tickers = tickers;
    }
}
