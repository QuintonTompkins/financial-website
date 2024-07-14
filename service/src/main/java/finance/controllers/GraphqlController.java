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

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import finance.dao.CompanyFilingDao;
import finance.dao.CompanySummaryDao;
import finance.models.CompanyFiling;
import finance.models.CompanySummary;
import finance.models.GenericParameters;

@Controller
public class GraphqlController {
    protected static final Logger LOGGER = Logger.getLogger(GraphqlController.class.getName());
    
    @Autowired
    CompanyFilingDao companyFilingDao;
    @Autowired
    CompanySummaryDao companySummaryDao;
	
	@QueryMapping
    public List<CompanyFiling> companyFilings(@Argument GenericParameters filter){
        return companyFilingDao.getCompanyFilings(filter);
    }
	
	@QueryMapping
    public List<CompanySummary> companySummaries(@Argument GenericParameters filter){
        return companySummaryDao.getCompanySummaries(filter);
    }
}
