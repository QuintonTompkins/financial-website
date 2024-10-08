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

import * as FinanceApi from '@/services/FinanceApi.js'
import type { CompanyFiling } from '@/services/types/CompanyFiling'
</script>

<template>
    <div style="margin-left: 27px; margin-top: 10px">
        <v-btn-toggle color="secondary" v-model="filterType" borderless variant="flat">
            <v-btn value="profitable" class="recent-selection-button" :disabled="loadingTable != false">Profitable Annual Earnings</v-btn>
            <v-btn value="spinoffs" class="recent-selection-button" :disabled="loadingTable != false">Spin Offs</v-btn>
            <v-btn value="mergers" class="recent-selection-button" :disabled="loadingTable != false">Mergers and Acquisition</v-btn>
            <v-btn value="ipos" class="recent-selection-button" :disabled="loadingTable != false">IPOs</v-btn>
        </v-btn-toggle>
    </div>
    <div class="scrollable-tbody">
        <v-data-table-virtual
            :headers="companyFilingsHeaders"
            :items="companyFilings"
            :loading="loadingTable"
            :loading-text="loadingMessage"
            class="search-table"
            fixed-header>
            <template #item.cik="{ item }">
                <v-btn :to="'/company/'+item.cik" color="secondary" variant="text">{{item.cik}}</v-btn>
            </template>
        </v-data-table-virtual>
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
            companyFilingsHeaders: [
                {title: 'CIK', key: 'cik', width: '150px'},
                {title: 'Name', key: 'name', width: '450px'},
                {title: 'SIC Description', key: 'sicDescription', width: '450px'},
                {title: 'Accession Number', key: 'accessionNumber'},
                {title: 'Filing Date', key: 'filingDate'},
                {title: 'Report Date', key: 'reportDate'},
                {title: 'Form', key: 'form'}
            ],
            companyFilings: [] as CompanyFiling[],
            width: window.innerWidth,
            height: window.innerHeight,
            loadingTable: false as boolean | string,
            loadingMessage: 'Loading Filings...' as string,
            filterType: "" as String
        };
    },
    watch: {
        filterType(newVal, oldVal){
            this.getCompanyRecentFilings()
        }
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
        this.getCompanyRecentFilings()
    },

    methods: {
        async getCompanyRecentFilings(){
            switch(this.filterType){
                case "profitable":
                    this.getRecentProfitableAnnualEarnings()
                    break
                case "spinoffs":
                    this.getRecentSpinoffFilings()
                    break
                case "mergers":
                    this.getRecentMergersAndAcquisitionFilings()
                    break
                case "ipos":
                    this.getRecentIpoFilings()
                    break
            }
        },
        async getRecentProfitableAnnualEarnings(){
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
            recentGenericFilters.push({
                field: "form",
                comparator: "=",
                value: "10-K"
            })
            recentCompanyFilingDataFilter.push({
                field: "us-gaap_GrossProfit",
                comparator: ">",
                valueIsField: false,
                value: 0
            })
            const responseFilings = await FinanceApi.getRecentCompanyFilings(recentGenericFilters, recentCompanyFilingDataFilter, false)
            let companyFilings = responseFilings.data.data.companyFilings
            this.companyFilings = await this.getCompanySummary(companyFilings)
            this.loadingTable = false
        },
        async getRecentSpinoffFilings(){
            this.companyFilings = []
            this.loadingTable = "primary"
            let recentGenericFilters = [] as GenericFilter[]
            let recentCompanyFilingDataFilter = [] as CompanyFilingDataFilter[]
            let date = new Date();
            date.setDate(date.getDate() - 365);
            let filterDate: String = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' +  date.getDate()
            recentGenericFilters.push({
                field: "filing_date",
                comparator: ">",
                value: filterDate
            })
            recentGenericFilters.push({
                field: "form",
                comparator: "in",
                value: "10-12B,10-12B/A"
            })
            const responseFilings = await FinanceApi.getRecentCompanyFilings(recentGenericFilters, recentCompanyFilingDataFilter, false)
            let companyFilings = responseFilings.data.data.companyFilings
            this.companyFilings = await this.getCompanySummary(companyFilings)
            this.loadingTable = false
        },
        async getRecentMergersAndAcquisitionFilings(){
            this.companyFilings = []
            this.loadingTable = "primary"
            let recentGenericFilters = [] as GenericFilter[]
            let recentCompanyFilingDataFilter = [] as CompanyFilingDataFilter[]
            let date = new Date();
            date.setDate(date.getDate() - 365);
            let filterDate: String = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' +  date.getDate()
            recentGenericFilters.push({
                field: "filing_date",
                comparator: ">",
                value: filterDate
            })
            recentGenericFilters.push({
                field: "form",
                comparator: "in",
                value: "S-4,S-4/A"
            })
            const responseFilings = await FinanceApi.getRecentCompanyFilings(recentGenericFilters, recentCompanyFilingDataFilter, false)
            let companyFilings = responseFilings.data.data.companyFilings
            this.companyFilings = await this.getCompanySummary(companyFilings)
            this.loadingTable = false
        },
        async getRecentIpoFilings(){
            this.companyFilings = []
            this.loadingTable = "primary"
            let recentGenericFilters = [] as GenericFilter[]
            let recentCompanyFilingDataFilter = [] as CompanyFilingDataFilter[]
            let date = new Date();
            date.setDate(date.getDate() - 365);
            let filterDate: String = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' +  date.getDate()
            recentGenericFilters.push({
                field: "filing_date",
                comparator: ">",
                value: filterDate
            })
            recentGenericFilters.push({
                field: "form",
                comparator: "in",
                value: "S-1,S-1/A,S-3,S-3/A"
            })
            const responseFilings = await FinanceApi.getRecentCompanyFilings(recentGenericFilters, recentCompanyFilingDataFilter, false)
            let companyFilings = responseFilings.data.data.companyFilings
            this.companyFilings = await this.getCompanySummary(companyFilings)
            this.loadingTable = false
        },
        async getCompanySummary(records: any[]){
            for(let record of records){
                const response = await FinanceApi.getSmallCompanySummary(record.cik)
                record.name = response.data.data.companySummaries[0].name
                record.sicDescription = response.data.data.companySummaries[0].sicDescription
            }
            return records
        }
    }
});
</script>

<style scoped>
.search-table {
    width: v-bind((width-50) + 'px');
    height: v-bind((height-135) + 'px');
    margin-left: 25px;
    margin-top: 15px;
}
.recent-selection-button {
    border: 1px solid black !important;
}
</style>