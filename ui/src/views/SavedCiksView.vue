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
</script>

<template>
    <div class="scrollable-tbody">
        <v-data-table-virtual
            :headers="savedHeaders"
            :items="savedCiks"
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
            savedHeaders: [
                {title: 'CIK', key: 'cik', width: '150px'},
                {title: 'Name', key: 'name'}
            ],
            savedCiks: [] as {cik: String, name: String}[],
            width: window.innerWidth,
            height: window.innerHeight,
            loadingTable: false as boolean | string,
            loadingMessage: 'Loading Companies...' as string
        };
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
        this.getSavedCiks()
    },

    methods: {
        async getSavedCiks(){
            this.savedCiks = []
            this.loadingTable = "primary"
            const responseFilings = await UserApi.getSavedCiks(this.jwt)
            let savedCiks = responseFilings.data.data.savedCiks.map(function(cik) { return {cik: cik, name: ""} })
            this.savedCiks = await this.getCompanyNames(savedCiks)
            this.loadingTable = false
        },
        async getCompanyNames(records: any[]){
            for(let record of records){
                const nameResponse = await FinanceApi.getCompanyName(record.cik)
                record.name = nameResponse.data.data.companySummaries[0].name
            }
            return records
        }
    }
});
</script>

<style scoped>
.search-table {
    width: v-bind((width-50) + 'px');
    height: v-bind((height-80) + 'px');
    margin-left: 25px;
    margin-top: 15px;
}
</style>