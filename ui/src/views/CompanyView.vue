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
import type { CompanySummary } from '@/services/types/CompanySummary'
import type { UserComment } from '@/services/types/UserComment'
import type { CompanyFiling } from '@/services/types/CompanyFiling';
import type { CompanyFilingKey } from '@/services/types/CompanyFilingKey'
import type { CompanyFilingSelected } from '@/services/types/CompanyFilingExtensions'
</script>

<template>
    <div class="view" >
        <div class="left-column" >
            <v-card class="card">
                <div style="height: 175px; width: 460px;">
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
                <div class="filing-height" style="width: 460px;">
                    <v-card-title class="card-title">Company Filings</v-card-title>
                    <input type="checkbox" v-model="tenOnly" style="display: inline;">10-K/10-Q Only</input>
                    <br>
                    <div class="scrollable-tbody filing-list">
                        <table>
                            <thead>
                                <tr>
                                    <th>Accession Number</th>
                                    <th>Report Date</th>
                                    <th>Filing Date</th>
                                    <th>Form</th>
                                    <th>Selected</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="companyFiling in companyFilings" :key="companyFiling.accessionNumber">
                                    <td>{{ companyFiling.accessionNumber }}</td>
                                    <td>{{ companyFiling.reportDate }}</td>
                                    <td>{{ companyFiling.filingDate }}</td>
                                    <td>{{ companyFiling.form }}</td>
                                    <td><input type="checkbox" v-model="companyFiling.selected" /></td>
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
                    <v-card-title class="card-title">Company Analysis</v-card-title>
                    <button @click="setCompanyAnalysisView('summary')" >Summary</button>
                    <button @click="setCompanyAnalysisView('comment')" :disabled="!hasCommentorRole">Comments</button>
                    <br>
                    <div v-if="companyView=='summary'">
                        <div class="scrollable-tbody analysis">
                            <table>
                                <thead>
                                    <tr>
                                        <th></th>
                                        <th v-for="companyFiling in companyFilings.filter((cf)=>{return cf.selected})" :key="companyFiling.accessionNumber">
                                            {{ companyFiling.filingDate }}<br>{{ companyFiling.form }}
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="filingKey in selectedCompanyFilingKeys">
                                        <td>{{ filingKey }}</td>
                                        <td v-for="companyFiling in companyFilings.filter((cf)=>{return cf.selected})" :key="companyFiling.accessionNumber">
                                            {{convertUnit(companyFiling.data[filingKey]?.unit) + addCommas(companyFiling.data[filingKey]?.value) }}
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div v-if="companyView=='comment'"  style="margin: 10px;">
                        <div class="scrollable-comment-list">
                            <div v-for="userComment in userComments">
                                <v-card class="card">
                                    <div class="comment-list-item">
                                        <v-card-text class="card-text-sub-title">{{ userComment.userName }}</v-card-text>
                                        <v-card-text class="card-text-sub-title">Min Price: {{ userComment.minPrice }}</v-card-text>
                                        <v-card-text class="card-text-sub-title">Max Price: {{ userComment.maxPrice }}</v-card-text>
                                        <br>
                                        <v-card-text class="card-text" style="white-space: pre-line">{{ userComment.comment }}</v-card-text>
                                    </div>
                                </v-card>
                            </div>
                        </div>
                        <div>
                            <textarea v-model="newComment.minPrice" placeholder="Min Price"></textarea>
                            <textarea v-model="newComment.maxPrice" placeholder="Max Price"></textarea>
                            <textarea v-model="newComment.commentText" placeholder="Comment" style="width: 500px;"></textarea>
                            <button @click="submitComment">Submit</button>
                        </div>
                    </div>
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
            companyFilings: [] as CompanyFilingSelected[],
            userComments: [] as UserComment[],
            companyFilingKeys: [] as String[],
            selectedCompanyFilingKeys: [
                "us-gaap_GrossProfit",
                "us-gaap_NetIncomeLoss",
                "us-gaap_Assets",
                "us-gaap_Liabilities",
                "us-gaap_NetCashProvidedByUsedInOperatingActivities",
                "us-gaap_NetCashProvidedByUsedInFinancingActivities",
                "us-gaap_NetCashProvidedByUsedInInvestingActivities",
                "dei_EntityCommonStockSharesOutstanding"
            ] as string[],
            width: window.innerWidth,
            height: window.innerHeight,
            cikIsSaved: false as Boolean,
            companyView: "summary" as String,
            newComment: {
                minPrice: -1 as number,
                maxPrice: -1 as number,
                commentText: "" as string
            },
            hasCommentorRole: false as boolean,
            tenOnly: true as boolean
        };
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
        this.getCompanySummary()
        this.getCompanyFilings()
        if(this.jwt != ""){
            this.getSavedCiks()
        }
    },
    watch: {
        tenOnly(newVal, oldVal){
            this.getCompanyFilings()
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
    methods: {
        convertUnit(unit: String){
            switch(unit){
                case "USD":
                    return "$"
                default:
                    return " "
            }
        },
        addCommas(value: String){
            if(value){
                return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
            }
            else{
                return ""
            }
        },
        getCompanySummary(){
            FinanceApi.getCompanySummary(this.cik).then((response: { data: { data: { companySummaries: CompanySummary[] } }; status: number; }) => {
                this.companySummary = response.data.data.companySummaries[0]
            })
        },
        getCompanyFilings(){
            let recentGenericFilters = [] as GenericFilter[]
            recentGenericFilters.push({
                field: "cik",
                comparator: "=",
                value: this.cik
            })
            if(this.tenOnly){
                recentGenericFilters.push({
                    field: "form",
                    comparator: "like",
                    value: "10-%"
                })
            }
            FinanceApi.getRecentCompanyFilings(recentGenericFilters, [], true).then((response: { data: { data: { companyFilings: CompanyFiling[] } }; status: number; }) => {
                this.companyFilings = response.data.data.companyFilings
                for(let filing of this.companyFilings){
                    if(filing.form === "10-K")
                        filing.selected = true
                }
            })
        },
        getCompanyFilingKeys(keyFilter: String){
            FinanceApi.getCompanyFilingKeys(keyFilter).then((response: { data: { data: { companyFilingKeys: CompanyFilingKey[] } }; status: number; }) => {
                this.companyFilingKeys = response.data.data.companyFilingKeys.map(function (keyObj) { return keyObj.key; });
            })
        },
        addToSavedCik(){
            UserApi.addToSavedCik(this.cik, this.jwt).then((response: { data: { data: String }; status: number; }) => {
                if(response.status == 200)
                    this.cikIsSaved = true
             })
        },
        removeSavedCik(){
            UserApi.removeSavedCik(this.cik, this.jwt).then((response: { data: { data: String }; status: number; }) => {
                if(response.status == 200)
                    this.cikIsSaved = false
             })
        },
        getSavedCiks(){
            UserApi.getSavedCiks(this.jwt).then((response: { data: { data: { savedCiks: String[] } }; status: number; }) => {
                this.cikIsSaved = response.data.data.savedCiks.includes(this.cik)
            })
        },
        setCompanyAnalysisView(view: String){
            this.companyView = view;
            if(view == 'comment')
                this.getComments()
        },
        submitComment(){
            UserApi.addComment(this.cik, this.newComment.minPrice, this.newComment.maxPrice, this.newComment.commentText,  this.jwt)
                .then((response: { data: { data: String }; status: number; }) => {
                    this.getComments()
            })
        },
        getComments(){
            UserApi.getCompanyComments(this.cik, this.jwt)
                .then((response: { data: { data: { userComments: UserComment[] } }; status: number; }) => {
                    this.userComments = response.data.data.userComments
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
    width: 485px;
}

.right-column {
    width: v-bind((width-510) + 'px');
}

.filing-height {
    height: v-bind((height-295) + 'px');
}

.analysis-height-width {
    width: v-bind((width-510) + 'px');
    height: v-bind((height-97) + 'px');
}

.analysis {
  height: v-bind((height-95) + 'px');
}

.filing-list {
  height: v-bind((height-330) + 'px');
}


.scrollable-tbody {
  overflow-y: auto;
}

table {
  margin: 5px;
  border-collapse: collapse;
}


thead {
  background-color: #1a1a1a;
  position: sticky;
  top: 0;
  z-index: 1;
}

th, td {
  border: 3px solid #1a1a1a;
}

.scrollable-comment-list {
  height: v-bind((height-200) + 'px');
  overflow-y: auto;
}
.comment-list-item{
    width: v-bind((width-565) + 'px');
    margin: 5px;
}
</style>