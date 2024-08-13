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
import type { UserComment } from './types/UserComment'

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

export function addComment(cik: String, minPrice: number, maxPrice: number, comment: String, jwt: any) {
    return axios.post<{ data: String }>(BASE_URL+'graphql',
        {
            "query": `mutation($cik: String!, $minPrice: Float!, $maxPrice: Float!, $comment: String!) {
                            addUserComment(cik: $cik, minPrice: $minPrice, maxPrice: $maxPrice, comment: $comment)
                        }`,
            "variables": {
                "cik": cik,
                "minPrice": minPrice,
                "maxPrice": maxPrice,
                "comment": comment
            }
        },
        { headers: { Authorization: jwt } }
    )
}

export function getCompanyComments(cik: String, jwt: any) {
    return axios.post<{ data: { userComments: UserComment[] } }>(BASE_URL+'graphql',
        {
            "query": `query ($input: GenericParameters!) {
                            userComments(input: $input) {
                                commentId,
                                userName,
                                comment,
                                minPrice,
                                maxPrice,
                                created,
                                voteTotal
                            }
                        }`,
            "variables": {
                "input": {
                    "filters": [
                      {
                        "field": "cik",
                        "comparator": "=",
                        "value": cik
                      }
                    ],
                    "sort": {
                      "field": "created",
                      "ascending": false
                    }
                }
              }
        },
        { headers: { Authorization: jwt } }
    )
}

export function getRecentComments(jwt: any) {
    return axios.post<{ data: { userComments: UserComment[] } }>(BASE_URL+'graphql',
        {
            "query": `query ($input: GenericParameters!) {
                            userComments(input: $input) {
                                cik,
                                commentId,
                                userId,
                                userName,
                                comment,
                                minPrice,
                                maxPrice,
                                created,
                                voteTotal
                            }
                        }`,
            "variables": {
                "input": {
                    "sort": {
                      "field": "created",
                      "ascending": false
                    }
                }
              }
        },
        { headers: { Authorization: jwt } }
    )
}

export function getUserComments(jwt: any, userId: number) {
    return axios.post<{ data: { userComments: UserComment[] } }>(BASE_URL+'graphql',
        {
            "query": `query ($input: GenericParameters!) {
                            userComments(input: $input) {
                                cik,
                                commentId,
                                userId,
                                userName,
                                comment,
                                minPrice,
                                maxPrice,
                                created,
                                voteTotal
                            }
                        }`,
            "variables": {
                "input": {
                    "filters": [
                      {
                        "field": "user_id",
                        "comparator": "=",
                        "value": userId
                      }
                    ],
                    "sort": {
                      "field": "created",
                      "ascending": false
                    }
                }
              }
        },
        { headers: { Authorization: jwt } }
    )
}

export function upVoteComment(jwt: any, commentId: number) {
    return axios.post<{ data: String }>(BASE_URL+'graphql',
        {
            "query": `mutation($commentId: Int!) {
                            upVoteComment(commentId: $commentId)
                        }`,
            "variables": {
                "commentId": commentId
              }
        },
        { headers: { Authorization: jwt } }
    )
}

export function downVoteComment(jwt: any, commentId: number) {
    return axios.post<{ data: String }>(BASE_URL+'graphql',
        {
            "query": `mutation($commentId: Int!) {
                            downVoteComment(commentId: $commentId)
                        }`,
            "variables": {
                "commentId": commentId
              }
        },
        { headers: { Authorization: jwt } }
    )
}

export function getCommentVotes(jwt: any, commentId: number) {
    return axios.post<{ data: { userComments: UserComment[] } }>(BASE_URL+'graphql',
        {
            "query": `query ($input: GenericParameters!) {
                            userComments(input: $input) {
                                voteTotal
                            }
                        }`,
            "variables": {
                "input": {
                    "filters": [
                      {
                        "field": "comment_id",
                        "comparator": "=",
                        "value": commentId
                      }
                    ]
                }
              }
        },
        { headers: { Authorization: jwt } }
    )
}
