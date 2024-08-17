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
import { jwtDecode } from "jwt-decode"

import * as FinanceApi from '@/services/FinanceApi.js'
import * as UserApi from '@/services/UserApi.js'
import type { CompanyFilingWithName } from '@/services/types/CompanyFilingExtensions'
import type { UserCommentWithName } from '@/services/types/UserCommentExtensions'
import UserComment from '@/components/UserComment.vue'
</script>

<template>
    <div style="margin:15px;">
        <v-card style="width: 387px;">
            <v-btn-toggle color="primary" v-model="searchType" borderless variant="outlined">
                <v-btn color="primary" size="small" value="recent" :disabled="loading == true">Recent Filings</v-btn>
                <v-btn color="primary" size="small" value="saved" :disabled="jwt=='' || loading == true">Saved Ciks</v-btn>
                <v-btn color="primary" size="small" value="comments" :disabled="!hasCommentorRole || loading == true">Recent Comments</v-btn>
            </v-btn-toggle>
        </v-card>
    </div>
    <div style="margin-left: 15px">
        <div v-if="searchType == 'recent'">
            <h3 style="display: inline">Recent Filings</h3>
            <input type="checkbox" v-model="profitOnly" style="display: inline;">Profitable Only</input>
            <input type="checkbox" v-model="annualOnly" style="display: inline;">10-K Only</input>
        </div>
        <div v-if="searchType == 'saved'">
            <h3>Saved Ciks</h3>
        </div>
        <div v-if="searchType == 'comments'">
            <h3>Recent Comments</h3>
        </div>
        <v-progress-linear v-if="loading" color="primary" indeterminate class="loader"></v-progress-linear>
    </div>
    <div v-if="!loading">
        <div v-if="searchType == 'recent'">
            <div class="scrollable-list">
                <div v-for="companyFiling in companyFilings">
                    <v-card style="margin: 10px;" variant="elevated">
                        <a class="a-hidden" :href="'/company/'+companyFiling.cik">
                            <div class="list-item" style="height: 30px;">
                                <div class="card-text-sub-title">CIK:</div><div class="card-text">{{ companyFiling.cik }}</div>
                                <div class="card-text-sub-title">Name:</div><div class="card-text">{{ companyFiling.name }}</div>
                                <div class="card-text-sub-title">Accession Number:</div><div class="card-text">{{ companyFiling.accessionNumber }}</div>
                                <div class="card-text-sub-title">Filing Date:</div><div class="card-text">{{ companyFiling.filingDate }}</div>
                                <div class="card-text-sub-title">Report Date:</div><div class="card-text">{{ companyFiling.reportDate }}</div>
                                <div class="card-text-sub-title">Form:</div><div class="card-text">{{ companyFiling.form }}</div>
                            </div>
                        </a>
                    </v-card>
                </div>
            </div>
        </div>
        <div v-if="searchType == 'saved'">
            <div class="scrollable-list">
                <div v-for="savedCik in savedCiks">
                    <v-card style="margin: 10px;" variant="elevated">
                        <a class="a-hidden" :href="'/company/'+savedCik.cik">
                            <div class="list-item" style="height: 30px;">
                                <div class="card-text-sub-title">CIK:</div><div class="card-text">{{ savedCik.cik }}</div>
                                <div class="card-text-sub-title">Name:</div><div class="card-text">{{ savedCik.name }}</div>
                            </div>
                        </a>
                    </v-card>
                </div>
            </div>
        </div>
        <div v-if="searchType == 'comments'">
            <div class="scrollable-list">
                <div v-for="comment in comments">
                    <v-card>
                        <div class="list-item">
                            <UserComment :comment="comment" :jwt="jwt" />
                        </div>
                    </v-card>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
export default defineComponent({
    name: 'SearchView',
    emits: ['updateJwt'],
    props: ["jwt"],
    components: {
    },
    data() {
        return {
            searchType: "" as String,
            companyFilings: [] as CompanyFilingWithName[],
            savedCiks: [] as {cik: String, name: String}[],
            comments: [] as UserCommentWithName[],
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
            
            let date = new Date();
            date.setDate(date.getDate() - 14);
            let filterDate: String = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' +  date.getDate()
            recentGenericFilters.push({
                field: "filing_date",
                comparator: ">",
                value: filterDate
            })

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
            await this.getCompanyNames(this.companyFilings)
            this.loading = false
        },
        async getSavedCiks(){
            this.savedCiks = []
            this.loading = true
            const responseFilings = await UserApi.getSavedCiks(this.jwt)
            this.savedCiks = responseFilings.data.data.savedCiks.map(function(cik) { return {cik: cik, name: ""} })
            await this.getCompanyNames(this.savedCiks)
            this.loading = false
        },
        async getComments(){
            this.comments = []
            this.loading = true
            const responseFilings = await UserApi.getRecentComments(this.jwt)
            this.comments = responseFilings.data.data.userComments
            await this.getCompanyNames(this.comments)
            this.loading = false
        },
        async getCompanyNames(records: any[]){
            for(let record of records){
                const nameResponse = await FinanceApi.getCompanyName(record.cik)
                record.name = nameResponse.data.data.companySummaries[0].name
            }
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
.loader{
    width: v-bind((width-50) + 'px');
    margin: 15px;
}
</style>