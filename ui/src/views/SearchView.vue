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

import * as stateCountryCodesJson from '@/assets/stateCountryCodes.json'
import * as FinanceApi from '@/services/FinanceApi.js'
import * as UserApi from '@/services/UserApi.js'
import type { CompanyFilingWithName } from '@/services/types/CompanyFilingExtensions'
import type { UserCommentWithName } from '@/services/types/UserCommentExtensions'
import type { CompanySummary } from '@/services/types/CompanySummary'
import type { SicDetails } from '@/services/types/SicDetails'
import type { LocationDataLatLng } from '@/services/types/LocationDataExtensions'
import UserComment from '@/components/UserComment.vue'
import Map from '@/components/Map.vue'
import type { StateCountryCodes } from '@/services/types/StateCountryCodes'
</script>

<template>
    <div style="margin-left:16px;">
        <v-card style="width: 600px;">
            <v-btn-toggle color="primary" v-model="searchType" borderless variant="outlined">
                <v-btn color="primary" size="small" value="name" :disabled="loading == true || loadingTable != false">Search Name</v-btn>
                <v-btn color="primary" size="small" value="recent" :disabled="loading == true || loadingTable != false">Recent Filings</v-btn>
                <v-btn color="primary" size="small" value="map" :disabled="loading == true || loadingTable != false">Map View</v-btn>
                <v-btn color="primary" size="small" value="saved" :disabled="jwt=='' || loading == true || loadingTable != false">Saved Ciks</v-btn>
                <v-btn color="primary" size="small" value="comments" :disabled="!hasCommentorRole || loading == true || loadingTable != false">Recent Comments</v-btn>
            </v-btn-toggle>
        </v-card>
    </div>
    <div style="margin-left: 15px; margin-top: 10px">
        <div v-if="searchType == 'name'" style="height: 40px;">
            <v-form style="display: inline-block;" @submit.prevent>
                <v-text-field placeholder="Search for company name" v-model="companyName" density="compact" style="display: inline-block; width: 500px; margin-left: 11px;"></v-text-field>
                <v-btn type="submit" color="primary" @click="getCompanyByName" style="display: inline-block; vertical-align: top; margin-left: 14px;">Search</v-btn>
            </v-form>
        </div>
        <div v-if="searchType == 'recent'">
            <div style="display: inline-block; vertical-align: bottom; margin-left: 5px;">
                <v-checkbox-btn label="Profitable Only" v-model="profitOnly" color="secondary"/>
            </div>
            <div style="display: inline-block; vertical-align: bottom; margin-left: 15px;">
                <v-checkbox-btn label="10-K Only" v-model="annualOnly" color="secondary"/>
            </div>
        </div>
        <v-progress-linear v-if="loading" color="primary" indeterminate class="loader"></v-progress-linear>
    </div>
    <div v-if="searchType == 'name'" class="scrollable-tbody">
        <v-data-table-virtual
            :headers="companyHeaders"
            :items="companies"
            :loading="loadingTable"
            :loading-text="loadingCompaniesMessage"
            class="search-table"
            fixed-header>
            <template #item.cik="{ item }">
                <a :href="'/company/'+item.cik">
                    <v-btn color="secondary">{{item.cik}}</v-btn>
                </a>
            </template>
        </v-data-table-virtual>
    </div>
    <div v-if="searchType == 'recent'" class="scrollable-tbody">
        <v-data-table-virtual
            :headers="companyFilingsHeaders"
            :items="companyFilings"
            :loading="loadingTable"
            :loading-text="loadingCompaniesMessage"
            class="search-table"
            fixed-header>
            <template #item.cik="{ item }">
                <a :href="'/company/'+item.cik">
                    <v-btn color="secondary">{{item.cik}}</v-btn>
                </a>
            </template>
        </v-data-table-virtual>
    </div>
    <div v-if="searchType == 'saved'" class="scrollable-tbody">
        <v-data-table-virtual
            :headers="savedHeaders"
            :items="savedCiks"
            :loading="loadingTable"
            :loading-text="loadingCompaniesMessage"
            class="search-table"
            fixed-header>
            <template #item.cik="{ item }">
                <a :href="'/company/'+item.cik">
                    <v-btn color="secondary">{{item.cik}}</v-btn>
                </a>
            </template>
        </v-data-table-virtual>
    </div>
    <div v-if="!loading">
        <div v-if="searchType == 'map'">
            <div class="scrollable-tbody" style="display: inline-block; width: 400px; vertical-align: top;">
                <v-data-table-virtual
                    v-if="sicDescSelected !== undefined"
                    :headers="companyHeaders"
                    :items="companies"
                    :loading="loadingTable"
                    :loading-text="loadingCompaniesMessage"
                    class="map-search-table"
                    fixed-header>
                    <template #item.cik="{ item }">
                        <a :href="'/company/'+item.cik">
                            <v-btn color="secondary" density="compact">{{item.cik}}</v-btn>
                        </a>
                    </template>
                </v-data-table-virtual>
                <v-data-table-virtual
                    v-if="sicDescSelected === undefined"
                    :headers="sicDetailsHeaders"
                    :items="sicDetails"
                    class="map-search-table"
                    fixed-header>
                    <template #item.sicDescription="{ item }">
                        <v-tooltip>
                            <template v-slot:activator="{ props }">
                                <v-btn v-bind="props" color="secondary" density="compact" style="width: 275px" @click="selectSicDetails(item.sicDescription)">
                                    {{item.sicDescription === "" ? "N/A" : item.sicDescription?.substring(0, 25)}}</v-btn>
                            </template>
                            <span>{{ item.sicDescription }}</span>
                        </v-tooltip>
                    </template>
                </v-data-table-virtual>
            </div>
            <div class="map">
                <Map :locations="locations" :jwt="jwt" :selectLocationCallback="selectLocationCallback"></Map>
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
            searchType: "name" as String,
            companyHeaders: [
                {title: 'CIK', key: 'cik', width: '150px'},
                {title: 'Name', key: 'name', width: '800px'}
            ],
            companies: [] as CompanySummary[],
            sicDetailsHeaders: [
                {title: 'SIC Description', key: 'sicDescription', width: '200px'},
                {title: 'Quantity', key: 'totalRecentlyActive', width: '50px'}
            ],
            stateCountryCodes: stateCountryCodesJson as StateCountryCodes,
            locations: [] as LocationDataLatLng[],
            sicDetails: [] as SicDetails[],
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
            loadingTable: false as boolean | string,
            hasCommentorRole: false as Boolean,
            companyName: "" as String,
            countryStateSelected: "" as String,
            sicDescSelected: undefined as string | undefined,
            loadingCompaniesMessage: 'Loading Companies...' as string
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
                case "map":
                    this.getLocationData()
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
                    this.searchType = "name"
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
        selectLocationCallback(location: LocationDataLatLng){
            this.sicDescSelected = undefined
            this.countryStateSelected = location.code
            this.sicDetails = location.sicDetails
        },
        selectSicDetails(sicDesc: string | undefined){
            if(sicDesc !== undefined){
                this.sicDescSelected = sicDesc
                this.getCompanyByCountrySicDesc()
            }
        },
        async getCompanyByCountrySicDesc(){
            this.companies = []
            this.loadingTable = "primary"
            const responseFilings = await FinanceApi.getCompanySummaryByCountrySicDesc(this.countryStateSelected, this.sicDescSelected)
            this.companies = responseFilings.data.data.companySummaries
            this.loadingTable = false
        },
        async getCompanyByName(){
            this.companies = []
            this.loadingTable = "primary"
            const responseFilings = await FinanceApi.getCompanySummaryByName(this.companyName)
            this.companies = responseFilings.data.data.companySummaries
            this.loadingTable = false
        },
        async getLocationData(){
            this.locations = []
            this.loading = true
            const responseFilings = await FinanceApi.getLocationData()
            this.locations = responseFilings.data.data.locationData
            this.locations.forEach(location => {
                if(this.stateCountryCodes.data[location.code]){
                    location.name = this.stateCountryCodes.data[location.code].name
                    location.latitude = this.stateCountryCodes.data[location.code].latitude
                    location.longitude = this.stateCountryCodes.data[location.code].longitude
                }
                else{
                    location.name = this.stateCountryCodes.data["XX"].name
                    location.latitude = this.stateCountryCodes.data["XX"].latitude
                    location.longitude = this.stateCountryCodes.data["XX"].longitude
                }
            });
            this.loading = false
        },
        async getCompanyRecentFilings(){
            this.companyFilings = []
            this.loadingTable = "primary"
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
            let companyFilings = responseFilings.data.data.companyFilings
            this.companyFilings = await this.getCompanyNames(companyFilings)
            this.loadingTable = false
        },
        async getSavedCiks(){
            this.savedCiks = []
            this.loadingTable = "primary"
            const responseFilings = await UserApi.getSavedCiks(this.jwt)
            let savedCiks = responseFilings.data.data.savedCiks.map(function(cik) { return {cik: cik, name: ""} })
            this.savedCiks = await this.getCompanyNames(savedCiks)
            this.loadingTable = false
        },
        async getComments(){
            this.comments = []
            this.loading = true
            const responseFilings = await UserApi.getRecentComments(this.jwt)
            let comments = responseFilings.data.data.userComments
            this.comments = await this.getCompanyNames(comments)
            this.loading = false
        },
        async getCompanyNames(records: any[]){
            for(let record of records){
                const nameResponse = await FinanceApi.getCompanyName(record.cik)
                record.name = nameResponse.data.data.companySummaries[0].name
            }
            return records
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
.map-search-table {
    width: v-bind((width-50) + 'px');
    height: v-bind((height-155) + 'px');
    margin-left: 25px;
    margin-top: 15px;
}
.map {
    width: v-bind((width-480) + 'px');
    height: v-bind((height-155) + 'px');
    display: inline-block;
    margin-left: 50px;
    margin-top: 15px;
    border: 2px solid black !important;
    box-shadow: 3px 6px 10px black !important;
}

</style>