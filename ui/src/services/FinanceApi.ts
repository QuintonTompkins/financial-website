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
import axios from 'axios'
import type { CompanySummary } from '@/services/types/CompanySummary'
import type { CompanyFiling } from '@/services/types/CompanyFiling'
import type { CompanyFilingKey } from '@/services/types/CompanyFilingKey'
import type { LocationData } from '@/services/types/LocationData'

const BASE_URL = import.meta.env.VITE_FINANCE_API_URL

export function getCompanySummary(cik: String){
    return axios.post<{ data: { companySummaries: CompanySummary[] } }>(BASE_URL+'graphql',
        {
            "query": `query ($input: GenericParameters!) {
                            companySummaries(input: $input) {
                                name
                                sicDescription
                                stateCountry
                                tickers {
                                exchange
                                ticker
                                }
                            }
                        }`,
            "variables": {
                "input":{ "filters": [ { "field": "cik" , "comparator": "=", "value": cik } ] }
            }
        }
    )
}

export function getCompanySummaryByCountrySicDesc(countryStateSelected: String, sicDescSelected: String | undefined) {
    return axios.post<{ data: { companySummaries: CompanySummary[] } }>(BASE_URL+'graphql',
        {
            "query": `query ($input: GenericParameters!) {
                            companySummaries(input: $input) {
                                cik,
                                name
                            }
                        }`,
            "variables": {
                "input":{   "filters": [ 
                                { "field": "state_country" , "comparator": "=", "value": countryStateSelected },
                                { "field": "sic_description" , "comparator": "=", "value": sicDescSelected }
                            ]
                }
            }
        }
    )
}


export function getCompanySummaryByName(name: String){
    return axios.post<{ data: { companySummaries: CompanySummary[] } }>(BASE_URL+'graphql',
        {
            "query": `query ($input: GenericParameters!) {
                            companySummaries(input: $input) {
                                cik,
                                name
                            }
                        }`,
            "variables": {
                "input":{ "filters": [ { "field": "name" , "comparator": "like", "value": "%"+name+"%" } ] }
            }
        }
    )
}

export function getLocationData(){
    return axios.post<{ data: { locationData: LocationData[] } }>(BASE_URL+'graphql',
        {
            "query": `query {
                            locationData {
                                code,
                                totalRecentlyActive,
                                sicDetails{
                                    sicDescription,
                                    totalRecentlyActive
                                }
                            }
                        }`
        }
    )
}

export function getRecentCompanyFilings(genericFilters: GenericFilter[], recentCompanyFilingDataFilter: CompanyFilingDataFilter[], withData: boolean) {
    let query = `query($input: CompanyFilingDataParameters!) {
        companyFilings(input: $input){
          cik,
          accessionNumber,
          form,
          filingDate,
          reportDate
        }
      }`
    if(withData){
        query = query.replace("reportDate","reportDate,data")
    }
    return axios.post<{ data: { companyFilings: CompanyFiling[] } }>(BASE_URL+'graphql',
        {
            "query": query,
            "variables": {
                "input":{
                    "filters": genericFilters,
                    "customFilters": recentCompanyFilingDataFilter,
                    "sort": {
                        "field": "filing_date",
                        "ascending": false
                    }
                }
            }
        }
    )
}

export function getCompanyFilingKeys(keyFilter: String) {
    return axios.post<{ data: { companyFilingKeys: CompanyFilingKey[] } }>(BASE_URL+'graphql',
        {
            "query": `query($input: GenericParameters!) {
                        companyFilingKeys(input: $input){
                            key
                        }
                    }`,
            "variables": {
                "input":{
                    "filters": [
                      { "field": "key" , "comparator": "like", "value": "%"+keyFilter+"%" }
                    ],
                    "sort": {
                        "field": "count",
                        "ascending": false
                    }
                }
            }
        }
    )
}


export function getCompanyName(cik: any) {
    return axios.post<{ data: { companySummaries: CompanySummary[] } }>(BASE_URL+'graphql',
        {
            "query": `query ($input: GenericParameters!) {
                            companySummaries(input: $input) {
                                name
                            }
                        }`,
            "variables": {
                "input":{ "filters": [ { "field": "cik" , "comparator": "=", "value": cik } ] }
            }
        }
    )
}