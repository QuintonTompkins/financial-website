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

package finance.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import finance.dao.CompanyFilingDao;
import finance.dao.CompanyFilingKeyDao;
import finance.dao.CompanySummaryDao;
import finance.dao.CompanyTickerDao;
import finance.dao.DataCollectorDao;
import finance.dao.LocationDataDao;
import finance.models.CompanyFiling;
import finance.models.CompanyFilingDataFilter;
import finance.models.CompanyFilingDataParameters;
import finance.models.CompanyFilingKey;
import finance.models.CompanySummary;
import finance.models.CompanyTicker;
import finance.models.GenericFilter;
import finance.models.GenericParameters;
import finance.models.LocationData;
import finance.models.SicDetails;

@Controller
public class FinanceController {
    protected static final Logger LOGGER = Logger.getLogger(FinanceController.class.getName());
    
    @Autowired
    CompanyFilingDao companyFilingDao;
    @Autowired
    CompanyFilingKeyDao companyFilingKeyDao;
    @Autowired
    CompanySummaryDao companySummaryDao;
    @Autowired
    CompanyTickerDao companyTickerDao;
    @Autowired
    DataCollectorDao dataCollectorDao;
    @Autowired
    LocationDataDao locationDataDao;
	
    // -------------------- Queries --------------------
	@QueryMapping
    public List<CompanyFiling> companyFilings(@Argument CompanyFilingDataParameters input){
        if(input != null && input.getCustomFilters() != null && input.getCustomFilters().size() > 0){
            for (CompanyFilingDataFilter filter : input.getCustomFilters()) {
                filter.validateFilter(companyFilingKeyDao);
            }
        }
        return companyFilingDao.getCompanyFilings(input);
    }

	@QueryMapping
    public List<CompanyFilingKey> companyFilingKeys(@Argument GenericParameters input){
        return companyFilingKeyDao.getCompanyFilingKeys(input);
    }
	
	@QueryMapping
    public List<CompanySummary> companySummaries(@Argument GenericParameters input){
        return companySummaryDao.getCompanySummaries(input);
    }

    @SchemaMapping(typeName="CompanySummary", field="tickers")
    public List<CompanyTicker> getTickers(CompanySummary companySummary) {
        GenericParameters params = new GenericParameters();
        GenericFilter filter = new GenericFilter("cik","=",companySummary.getCik());
        params.setFilters(Arrays.asList(filter));
        return companyTickerDao.getCompanyTickers(params);
    }
	
	@QueryMapping
    public List<CompanyTicker> companyTickers(@Argument GenericParameters input){
        return companyTickerDao.getCompanyTickers(input);
    }
	
	@QueryMapping
    public List<LocationData> locationData(){
        return locationDataDao.getLocationData();
    }

    @SchemaMapping(typeName="LocationData", field="sicDetails")
    public List<SicDetails> getSicDetails(LocationData locationData) {
        return locationDataDao.getSicDetails(locationData.getCode());
    }
	
	
	@QueryMapping
    public int timeSinceRefresh(){
        return dataCollectorDao.getTimeSinceRefresh();
    }

    // -------------------- Mutations --------------------
    
    
}
