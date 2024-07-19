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

export function createUser(userName: String, email: String, password: String){
    return axios.post<{ data: { createUser: String } }>(BASE_URL+'graphql',
        {
            "query": "mutation($userName: String!, $email: String!, $password: String!) { createUser(userName: $userName, email: $email, password: $password) }",
            "variables": {
                "userName": userName,
                "email": email,
                "password": password
            }
        }
    )
}

export function login(userNameEmail: String, password: String){
    return axios.post<{ data: { login: String } }>(BASE_URL+'graphql',
        {
            "query": "mutation($userNameEmail: String!, $password: String!) { login(userNameEmail: $userNameEmail, password: $password) }",
            "variables": {
                "userNameEmail": userNameEmail,
                "password": password
            }
        }
    )
}

export function updatePassword(email: String, oldPassword: String, newPassword: String){
    return axios.post<{ data: { updatePassword: String } }>(BASE_URL+'graphql',
        {
            "query": "mutation($email: String!, $oldPassword: String!, $newPassword: String!) { updatePassword(email: $email, oldPassword: $oldPassword, newPassword: $newPassword) }",
            "variables": {
                "email": email,
                "oldPassword": oldPassword,
                "newPassword": newPassword
            }
        }
    )
}

export function resetPassword(email: String){
    return axios.post<{ data: { resetPassword: String } }>(BASE_URL+'graphql',
        {
            "query": "mutation($email: String!) { resetPassword(email: $email) }",
            "variables": {
                "email": email
            }
        }
    )
}

export function updateUsername(userName: String, jwt: any){
    return axios.post<{ data: { updateUserName: String }  }>(BASE_URL+'graphql',
        {
            "query": "mutation($userName: String!) { updateUserName(userName: $userName) }",
            "variables": {
                "userName": userName
            }
        },
        { headers: { Authorization: jwt } }
    )
}

export function updateEmail(email: String, jwt: any){
    return axios.post<{ }>(BASE_URL+'graphql',
        {
            "query": "mutation($email: String!) { updateEmail(email: $email) }",
            "variables": {
                "email": email
            }
        },
        { headers: { Authorization: jwt } }
    )
}
