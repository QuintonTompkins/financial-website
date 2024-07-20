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

export function getRecentCompanyFilings() {
    return axios.post<{ data: { companyFilings: CompanyFiling[] } }>(BASE_URL+'graphql',
        {
            "query": `query($input: CompanyFilingDataParameters!) {
                companyFilings(input: $input){
                  cik,
                  accessionNumber,
                  form,
                  filingDate,
                  reportDate
                }
              }`,
            "variables": {
                "input":{
                    "sort": {
                        "field": "filing_date",
                        "ascending": false
                    }
                }
            }
        }
    )
}

export function getCompanyFilings(cik: String) {
    return axios.post<{ data: { companyFilings: CompanyFiling[] } }>(BASE_URL+'graphql',
        {
            "query": `query($input: CompanyFilingDataParameters!) {
                companyFilings(input: $input){
                  accessionNumber,
                  form,
                  filingDate,
                  reportDate,
                  data
                }
              }`,
            "variables": {
                "input":{
                    "filters": [ 
                        { "field": "cik" , "comparator": "=", "value": cik } 
                    ],
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