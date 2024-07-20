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

const BASE_URL = import.meta.env.VITE_FINANCE_API_URL

export function getSavedCiks(jwt: any) {
    return axios.post<{ data: { savedCiks: String[] } }>(BASE_URL+'graphql',
        {
            "query": `query {
                        savedCiks
                        }`
        },
        { headers: { Authorization: jwt } }
    )
}

export function addToSavedCik(cik: String, jwt: any) {
    return axios.post<{ data: String }>(BASE_URL+'graphql',
        {
            "query": `mutation($cik: String!) {
                            addToSavedCikList(cik: $cik)
                        }`,
            "variables": {
                "cik": cik
            }
        },
        { headers: { Authorization: jwt } }
    )
}

export function removeSavedCik(cik: String, jwt: any) {
    return axios.post<{ data: String }>(BASE_URL+'graphql',
        {
            "query": `mutation($cik: String!) {
                            deleteFromSavedCikList(cik: $cik)
                        }`,
            "variables": {
                "cik": cik
            }
        },
        { headers: { Authorization: jwt } }
    )
}

