<!--
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
-->
<script setup lang="ts">
import { defineComponent } from 'vue'
import { jwtDecode } from "jwt-decode";

import * as FinanceApi from '@/services/FinanceApi.js'
import * as UserApi from '@/services/UserApi.js'
import type { CompanyFilingWithName } from '@/services/types/CompanyFilingExtensions';
import type { UserComment } from '@/services/types/UserComment';
</script>

<template>
    <div style="margin:15px;">
        <button @click="searchType='recent'">Recent Filings</button>
        <button @click="searchType='saved'" :disabled="jwt==''">Saved Ciks</button>
        <button @click="searchType='comments'" :disabled="!hasCommentorRole">Recent Comments</button>
    </div>
    <div v-if="searchType == 'recent'">
        <h3 style="margin-left: 15px; display: inline;">Recent Filings</h3>
        <input type="checkbox" v-model="profitOnly" style="display: inline;">Profitable Only</input>
        <input type="checkbox" v-model="annualOnly" style="display: inline;">10-K Only</input>
        <div v-if="loading" class="loader"></div> 
        <div v-if="!loading" class="scrollable-list">
            <div v-for="companyFiling in companyFilings">
                <v-card class="card" @click="goToCompanyPage(companyFiling.cik)">
                    <div class="list-item" style="height: 25px;">
                        <v-card-text class="card-text-sub-title">CIK:</v-card-text><v-card-text class="card-text">{{ companyFiling.cik }}</v-card-text>
                        <v-card-text class="card-text-sub-title">Name:</v-card-text><v-card-text class="card-text">{{ companyFiling.name }}</v-card-text>
                        <v-card-text class="card-text-sub-title">Accession Number:</v-card-text><v-card-text class="card-text">{{ companyFiling.accessionNumber }}</v-card-text>
                        <v-card-text class="card-text-sub-title">Filing Date:</v-card-text><v-card-text class="card-text">{{ companyFiling.filingDate }}</v-card-text>
                        <v-card-text class="card-text-sub-title">Report Date:</v-card-text><v-card-text class="card-text">{{ companyFiling.reportDate }}</v-card-text>
                        <v-card-text class="card-text-sub-title">Form:</v-card-text><v-card-text class="card-text">{{ companyFiling.form }}</v-card-text>
                    </div>
                </v-card>
            </div>
        </div>
    </div>
    <div v-if="searchType == 'saved'">
        <h3 style="margin-left: 15px">Saved Ciks</h3>
        <div class="scrollable-list">
            <div v-for="savedCik in savedCiks">
                <v-card class="card" @click="goToCompanyPage(savedCik)">
                    <div class="list-item" style="height: 25px;">
                        <v-card-text class="card-text-sub-title">CIK:</v-card-text><v-card-text class="card-text">{{ savedCik }}</v-card-text>
                    </div>
                </v-card>
            </div>
        </div>
    </div>
    <div v-if="searchType == 'comments'">
        <h3 style="margin-left: 15px">Recent Comments</h3>
        <div class="scrollable-list">
            <div v-for="comment in comments">
                <v-card class="card">
                    <div class="list-item">
                        <v-card-text class="card-text-sub-title">CIK:</v-card-text><v-card-text class="card-text" @click="goToCompanyPage(comment.cik)">{{ comment.cik }}</v-card-text>
                        <v-card-text class="card-text-sub-title">Username:</v-card-text><v-card-text class="card-text" @click="goToUserPage(comment.userId)">{{ comment.userName }}</v-card-text>
                        <v-card-text class="card-text-sub-title">Created:</v-card-text><v-card-text class="card-text">{{ comment.created }}</v-card-text>
                        <br>
                        <v-card-text class="card-text" style="white-space: pre-line">{{comment.comment}}</v-card-text>
                    </div>
                </v-card>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
export default defineComponent({
    name: 'SearchView',
    props: ["jwt"],
    components: {
    },
    data() {
        return {
            searchType: "" as String,
            companyFilings: [] as CompanyFilingWithName[],
            savedCiks: [] as String[],
            comments: [] as UserComment[],
            width: window.innerWidth,
            height: window.innerHeight,
            profitOnly: true as Boolean,
            annualOnly: true as Boolean,
            loading: false as Boolean,
            hasCommentorRole: false as Boolean
        };
    },
    watch: {
        profitOnly(newVal, oldVal){
            this.getCompanyRecentFilings()
        },
        annualOnly(newVal, oldVal){
            this.getCompanyRecentFilings()
        },
        searchType(newVal, oldVal) {
            switch(newVal){
                case "recent":
                    this.getCompanyRecentFilings()
                    break;
                case "saved":
                    this.getSavedCiks()
                    break;
                case "comments":
                    this.getComments()
                    break;
            }
        },
        jwt: {
            immediate: true, 
            handler (newVal, oldVal) {
                if(newVal != ""){
                    const claims: {exp: number, sub: string, roles: String[]} = jwtDecode(newVal)
                    this.hasCommentorRole = claims.roles.includes('commentor')
                }
                else{
                    this.hasCommentorRole = false
                }
            }
        }
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
    },

    methods: {
        async getCompanyRecentFilings(){
            this.companyFilings = []
            this.loading = true
            let recentGenericFilters = [] as GenericFilter[]
            let recentCompanyFilingDataFilter = [] as CompanyFilingDataFilter[]
            if(this.annualOnly){
                recentGenericFilters.push({
                    field: "form",
                    comparator: "=",
                    value: "10-K"
                })
            }
            if(this.profitOnly){
                recentCompanyFilingDataFilter.push({
                    field: "us-gaap_GrossProfit",
                    comparator: ">",
                    valueIsField: false,
                    value: 0
                })
            }
            const responseFilings = await FinanceApi.getRecentCompanyFilings(recentGenericFilters, recentCompanyFilingDataFilter, false)
            this.companyFilings = responseFilings.data.data.companyFilings
            await this.getCompanyNames()
            this.loading = false
        },
        async getCompanyNames(){
            for(let filing of this.companyFilings){
                const nameResponse = await FinanceApi.getCompanyName(filing.cik)
                filing.name = nameResponse.data.data.companySummaries[0].name
            }
        },
        getSavedCiks(){
            UserApi.getSavedCiks(this.jwt).then((response: { data: { data: { savedCiks: String[] } }; status: number; }) => {
                this.savedCiks = response.data.data.savedCiks
            })
        },
        getComments(){
            UserApi.getRecentComments(this.jwt).then((response: { data: { data: { userComments: UserComment[] } }; status: number; }) => {
                this.comments = response.data.data.userComments
            })
        },
        goToCompanyPage(cik?: String){
            if(cik != null)
                this.$router.push(`/company/${cik}`)
        },
        goToUserPage(userId?: number){
            if(userId != null)
                this.$router.push(`/user/${userId}`)
        }
    }
});
</script>

<style scoped>
.scrollable-list {
  height: v-bind((height-160) + 'px');
  overflow-y: auto;
}
.list-item{
    width: v-bind((width-50) + 'px');
}
</style>