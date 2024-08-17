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
import UserCommentComp from '@/components/UserComment.vue'
</script>

<template>
    <div class="view" >
        <div class="left-column" >
            <v-card style="margin: 10px;">
                <div style="height: 195px; width: 480px;">
                    <div class="card-title">Company Details</div>
                    <v-icon color="green" icon="mdi-bookmark-plus" size="large" v-if="!cikIsSaved" @click="addToSavedCik" :disabled="jwt==''"></v-icon>
                    <v-icon color="red" icon="mdi-bookmark-remove" size="large" v-if="cikIsSaved" @click="removeSavedCik" :disabled="jwt==''"></v-icon>
                    <br>
                    <div class="card-text-sub-title company-details">Company Cik:</div>
                        <a :href="`https://www.sec.gov/edgar/browse/?CIK=${cik}`">{{ cik }}</a>
                        <br>
                    <div class="card-text-sub-title company-details">Company Name:</div>
                        <div class="card-text company-details">{{ companySummary.name }}</div>
                        <br>
                    <div class="card-text-sub-title company-details">Company Description:</div>
                        <div class="card-text company-details">{{ companySummary.sicDescription }}</div>
                        <br>
                    <div class="card-text-sub-title company-details">Company State/Country:</div>
                        <div class="card-text company-details">{{ companySummary.stateCountry }}</div>
                        <br>
                    <div class="card-text-sub-title company-details">Company Exchange/Tickers:</div>
                        <div class="card-text company-details">
                            <div  class="card-text company-details" v-for="ticker in companySummary.tickers" :key="ticker.exchange">
                                <a v-if="ticker.exchange=='NYSE' || ticker.exchange=='Nasdaq'"
                                    :href="`https://finviz.com/quote.ashx?t=${ticker.ticker}`">({{ ticker.exchange }}:{{ ticker.ticker }})</a>
                                <div v-if="!(ticker.exchange=='NYSE' || ticker.exchange=='Nasdaq')">({{ ticker.exchange }}:{{ ticker.ticker }})</div>
                            </div>
                        </div>
                </div>
            </v-card>
            <v-card style="margin: 10px; margin-top: 23px;">
                <div class="filing-height" style="width: 480px;">
                    <div class="card-title">Company Filings</div>
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
            <v-card style="margin: 10px;">
                <div class="analysis-height-width">
                    <div class="card-title" style="margin-left: 10px; vertical-align: top;">Company Analysis</div>
                    <v-card class="analysis-toggle" variant="tonal">
                        <v-btn-toggle v-model="companyView" color="primary" variant="outlined">
                            <v-btn size="small" value="summary" >Summary</v-btn>
                            <v-btn size="small" value="comment" :disabled="!hasCommentorRole">Comments</v-btn>
                        </v-btn-toggle>
                    </v-card>
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
                    <div v-if="companyView=='comment'" style="margin-right: 20px;">
                        <div class="scrollable-comment-list">
                            <v-card style="margin: 10px;" variant="tonal" v-for="userComment in userComments">
                                <div class="comment-list-item">
                                    <UserCommentComp :comment="userComment" :jwt="jwt"/>
                                </div>
                            </v-card>
                        </div>
                        <div style="margin-top: 15px;">
                            <v-text-field v-model="newComment.minPrice" placeholder="Min Price" style="margin-left: 5px; width: 120px; display: inline-block;"></v-text-field>
                            <v-text-field v-model="newComment.maxPrice" placeholder="Max Price" style="margin-left: 5px; width: 120px; display: inline-block;"></v-text-field>
                            <v-text-field v-model="newComment.commentText" placeholder="Comment" style="margin-left: 5px; width: 500px; display: inline-block;" maxlength="10000"></v-text-field>
                            <v-btn color="primary" @click="submitComment" style="margin-left: 5px; display: inline-block; vertical-align: top;">Submit</v-btn>
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
        companyView(newVal, oldVal){
            if(newVal == 'comment')
                this.getComments()
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
.company-details{
    margin-left: 5px;
    margin-top: 1px;
    margin-bottom: 1px;
}

.view {
    display: flex;
}

.left-column {
    width: 505px;
}

.right-column {
    width: v-bind((width-530) + 'px');
}

.filing-height {
    height: v-bind((height-319) + 'px');
}

.analysis-height-width {
    width: v-bind((width-530) + 'px');
    height: v-bind((height-97) + 'px');
}

.analysis {
  height: v-bind((height-145) + 'px');
}

.filing-list {
  height: v-bind((height-355) + 'px');
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
  height: v-bind((height-250) + 'px');
  overflow-y: auto;
}
.comment-list-item{
    width: v-bind((width-605) + 'px');
    margin: 5px;
}

.analysis-toggle {
    width: 190px;
    display: inline-block;
    margin-left: 25px;
    margin-top: 5px;
    vertical-align: bottom;
}
</style>