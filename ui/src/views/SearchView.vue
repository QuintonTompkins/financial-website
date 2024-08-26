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
            <div style="display: inline-block; vertical-align: bottom;">
                <v-checkbox-btn label="Profitable Only" v-model="profitOnly" color="primary"/>
            </div>
            <div style="display: inline-block; vertical-align: bottom;">
                <v-checkbox-btn label="10-K Only" v-model="annualOnly" color="primary"/>
            </div>
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
        <div v-if="searchType == 'recent'" class="scrollable-tbody">
            <v-data-table-virtual
                :headers="companyFilingsHeaders"
                :items="companyFilings"
                class="search-table"
                fixed-header>
                <template #item.cik="{ item }">
                    <a :href="'/company/'+item.cik">
                        <v-btn color="primary">{{item.cik}}</v-btn>
                    </a>
                </template>
            </v-data-table-virtual>
        </div>
        <div v-if="searchType == 'saved'" class="scrollable-tbody">
            <v-data-table-virtual
                :headers="savedHeaders"
                :items="savedCiks"
                class="search-table"
                fixed-header>
                <template #item.cik="{ item }">
                    <a :href="'/company/'+item.cik">
                        <v-btn color="primary">{{item.cik}}</v-btn>
                    </a>
                </template>
            </v-data-table-virtual>
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
            companyFilingsHeaders: [
                {title: 'CIK', key: 'cik', width: '150px'},
                {title: 'Name', key: 'name', width: '800px'},
                {title: 'Accession Number', key: 'accessionNumber'},
                {title: 'Filing Date', key: 'filingDate'},
                {title: 'Report Date', key: 'reportDate'},
                {title: 'Form', key: 'form'}
            ],
            companyFilings: [] as CompanyFilingWithName[],
            savedHeaders: [
                {title: 'CIK', key: 'cik', width: '150px'},
                {title: 'Name', key: 'name'}
            ],
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
                    this.searchType = ''
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
            date.setDate(date.getDate() - 30);
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
  height: v-bind((height-175) + 'px');
  overflow-y: auto;
}
.list-item{
    width: v-bind((width-50) + 'px');
}
.loader{
    width: v-bind((width-50) + 'px');
    margin: 15px;
}
.search-table {
    width: v-bind((width-50) + 'px');
    height: v-bind((height-215) + 'px');
    margin-left: 25px;
    margin-top: 15px;
}

</style>