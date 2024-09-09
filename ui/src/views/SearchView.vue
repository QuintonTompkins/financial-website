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
import type { CompanySummary } from '@/services/types/CompanySummary'
</script>

<template>
    <div style="margin-left: 15px; margin-top: 20px">
        <v-form style="display: inline-block;" @submit.prevent>
            <v-text-field placeholder="Company Name" v-model="companyName" density="compact" style="display: inline-block; width: 500px; margin-left: 11px;"></v-text-field>
            <v-btn type="submit" color="primary" @click="getCompanyByName" style="display: inline-block; vertical-align: top; margin-left: 14px;">Search</v-btn>
        </v-form>
    </div>
    <div class="scrollable-tbody">
        <v-data-table-virtual
            :headers="companyHeaders"
            :items="companies"
            :loading="loadingTable"
            :loading-text="loadingMessage"
            class="search-table"
            fixed-header>
            <template #item.cik="{ item }">
                <a :href="'/company/'+item.cik">
                    <v-btn color="secondary" variant="text">{{item.cik}}</v-btn>
                </a>
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
            companyHeaders: [
                {title: 'CIK', key: 'cik', width: '150px'},
                {title: 'Name', key: 'name', width: '800px'}
            ],
            companies: [] as CompanySummary[],
            width: window.innerWidth,
            height: window.innerHeight,
            loadingTable: false as boolean | string,
            companyName: "" as String,
            hasCommentorRole: false as Boolean,
            loadingMessage: 'Loading Companies...' as string
        };
    },
    watch: {
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
        async getCompanyByName(){
            this.companies = []
            this.loadingTable = "primary"
            const responseFilings = await FinanceApi.getCompanySummaryByName(this.companyName)
            this.companies = responseFilings.data.data.companySummaries
            this.loadingTable = false
        }
    }
});
</script>

<style scoped>
.search-table {
    width: v-bind((width-50) + 'px');
    height: v-bind((height-155) + 'px');
    margin-left: 25px;
}

</style>