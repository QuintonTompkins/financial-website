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
import * as UserApi from '@/services/UserApi.js'
import type { CompanySummary } from '@/services/types/CompanySummary'
import type { CompanyFiling } from '@/services/types/CompanyFiling';
import type { CompanyFilingKey } from '@/services/types/CompanyFilingKey'
</script>

<template>
    <div class="view" >
        <div class="left-column" >
            <v-card class="card">
                <div style="height: 175px; width: 400px;">
                    <v-card-title class="card-title">Company Details</v-card-title>
                    <button v-if="!cikIsSaved" @click="addToSavedCik" :disabled="jwt==''">add to saved ciks</button>
                    <button v-if="cikIsSaved" @click="removeSavedCik" :disabled="jwt==''">remove from saved ciks</button>
                    <br>
                    <v-card-text class="card-text-sub-title">Company Cik:</v-card-text>
                        <a :href="`https://www.sec.gov/edgar/browse/?CIK=${cik}`">{{ cik }}</a>
                        <br>
                    <v-card-text class="card-text-sub-title">Company Name:</v-card-text>
                        <v-card-text class="card-text">{{ companySummary.name }}</v-card-text>
                        <br>
                    <v-card-text class="card-text-sub-title">Company Description:</v-card-text>
                        <v-card-text class="card-text">{{ companySummary.sicDescription }}</v-card-text>
                        <br>
                    <v-card-text class="card-text-sub-title">Company State/Country:</v-card-text>
                        <v-card-text class="card-text">{{ companySummary.stateCountry }}</v-card-text>
                        <br>
                    <v-card-text class="card-text-sub-title">Company Exchange/Tickers:</v-card-text>
                        <v-card-text class="card-text"><br>
                            <div  class="card-text" style="display: inline;" v-for="ticker in companySummary.tickers" :key="ticker.exchange">
                                <a v-if="ticker.exchange=='NYSE' || ticker.exchange=='Nasdaq'"
                                    :href="`https://finviz.com/quote.ashx?t=${ticker.ticker}`">({{ ticker.exchange }}:{{ ticker.ticker }})</a>
                                <div v-if="!(ticker.exchange=='NYSE' || ticker.exchange=='Nasdaq')">({{ ticker.exchange }}:{{ ticker.ticker }})</div>
                            </div>
                        </v-card-text>
                </div>
            </v-card>
            <v-card class="card">
                <div class="filing-height" style="width: 400px;">
                    <v-card-title class="card-title">Company Filings</v-card-title><br>
                    <div class="scrollable-tbody">
                        <table>
                            <thead>
                                <tr>
                                    <th>Accession Number</th>
                                    <th>Report Date</th>
                                    <th>Filing Date</th>
                                    <th>Form</th>
                                </tr>
                            </thead>
                                <tbody>
                                <tr v-for="companyFiling in companyFilings" :key="companyFiling.accessionNumber">
                                    <td>{{ companyFiling.accessionNumber }}</td>
                                    <td>{{ companyFiling.reportDate }}</td>
                                    <td>{{ companyFiling.filingDate }}</td>
                                    <td>{{ companyFiling.form }}</td>
                                </tr>
                                </tbody>
                        </table>
                    </div>
                </div>
            </v-card>
        </div>
        <div class="right-column" >
            <v-card class="card">
                <div class="analysis-height-width">
                    <v-card-title class="card-title">Company Analysis</v-card-title><br>
                </div>
            </v-card>
        </div>
    </div>
</template>

<script lang="ts">
export default defineComponent({
    name: 'CompanyView',
    props: ["jwt"],
    components: {
    },
    data() {
        return {
            cik: this.$route.params.cik as String,
            companySummary: {} as CompanySummary,
            companyFilings: [] as CompanyFiling[],
            companyFilingKeys: [] as String[],
            width: window.innerWidth,
            height: window.innerHeight,
            cikIsSaved: false as Boolean
        };
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
        this.getCompanySummary()
        this.getCompanyFilings()
        this.getSavedCiks()
    },

    methods: {
        getCompanySummary(){
            FinanceApi.getCompanySummary(this.cik).then((response: { data: { data: { companySummaries: CompanySummary[] } }; status: number; }) => {
                this.companySummary = response.data.data.companySummaries[0]
            })
        },
        getCompanyFilings(){
            FinanceApi.getCompanyFilings(this.cik).then((response: { data: { data: { companyFilings: CompanyFiling[] } }; status: number; }) => {
                this.companyFilings = response.data.data.companyFilings
            })
        },
        getCompanyFilingKeys(keyFilter: String){
            FinanceApi.getCompanyFilingKeys(keyFilter).then((response: { data: { data: { companyFilingKeys: CompanyFilingKey[] } }; status: number; }) => {
                this.companyFilingKeys = response.data.data.companyFilingKeys.map(function (keyObj) { return keyObj.key; });
            })
        },
        addToSavedCik(){
            UserApi.addToSavedCik(this.cik, this.jwt).then((response: { data: { data: String }; status: number; }) => {
                console.log(response.data.data)
             })
        },
        removeSavedCik(){
            UserApi.removeSavedCik(this.cik, this.jwt).then((response: { data: { data: String }; status: number; }) => {
                console.log(response.data.data)
             })
        },
        getSavedCiks(){
            UserApi.getSavedCiks(this.jwt).then((response: { data: { data: { savedCiks: String[] } }; status: number; }) => {
                this.cikIsSaved = response.data.data.savedCiks.includes(this.cik)
            })
        }
    }
});
</script>

<style scoped>
.view {
    display: flex;
}

.left-column {
    width: 425px;
}

.right-column {
    width: v-bind((width-490) + 'px');
}

.filing-height {
    height: v-bind((height-295) + 'px');
}

.analysis-height-width {
    width: v-bind((width-460) + 'px');
    height: v-bind((height-97) + 'px');
}

.scrollable-tbody {
  height: v-bind((height-330) + 'px');
  overflow-y: auto;
}

table {
  margin: 5px;
  border-collapse: collapse;
}


thead {
  background-color: #0e3b3b;
  position: sticky;
  top: 0;
  z-index: 1;
}

th, td {
  border: 1px solid #0e3b3b;
}

</style>