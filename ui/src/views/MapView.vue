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

import * as stateCountryCodesJson from '@/assets/stateCountryCodes.json'
import * as FinanceApi from '@/services/FinanceApi.js'
import type { CompanySummary } from '@/services/types/CompanySummary'
import type { SicDetails } from '@/services/types/SicDetails'
import type { LocationDataLatLng } from '@/services/types/LocationDataExtensions'
import Map from '@/components/Map.vue'
import type { StateCountryCodes } from '@/services/types/StateCountryCodes'
</script>

<template>
    <div style="margin-left: 15px; margin-top: 10px">
        <v-progress-linear v-if="loading" color="primary" indeterminate class="loader"></v-progress-linear>
    </div>
    <div v-if="!loading">
        <div class="scrollable-tbody" style="display: inline-block; width: 400px; vertical-align: top;">
            <div v-if="sicDescSelected !== undefined" style="margin-left: 25px; max-height: 37px;">
                <v-btn icon="mdi-arrow-left" style="display: inline-block; vertical-align: top;" color="secondary" size="small" variant="outlined" @click="goBack">
                </v-btn>
                <div style="display: inline-block; width: 320px; margin-left: 15px;">
                    {{ sicDescSelected }} in {{ stateCountryCodes.data[countryStateSelected].name }}
                </div>
            </div>
            <v-data-table-virtual
                v-if="sicDescSelected !== undefined"
                :headers="companyHeaders"
                :items="companies"
                :loading="loadingTable"
                :loading-text="loadingCompaniesMessage"
                class="companies-table"
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
                class="industry-table"
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
            sicDetailsHeaders: [
                {title: 'SIC Description', key: 'sicDescription', width: '200px'},
                {title: 'Quantity', key: 'totalRecentlyActive', width: '50px'}
            ],
            stateCountryCodes: stateCountryCodesJson as StateCountryCodes,
            locations: [] as LocationDataLatLng[],
            sicDetails: [] as SicDetails[],
            width: window.innerWidth,
            height: window.innerHeight,
            loading: false as Boolean,
            loadingTable: false as boolean | string,
            countryStateSelected: "" as string,
            sicDescSelected: undefined as string | undefined,
            loadingCompaniesMessage: 'Loading Companies...' as string
        };
    },
    watch: {
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
        this.getLocationData()
    },

    methods: {
        goBack(){
            this.sicDescSelected = undefined
        },
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
        }
    }
});
</script>

<style scoped>
.loader{
    width: v-bind((width-50) + 'px');
    margin: 15px;
}
.companies-table {
    width: v-bind((width-50) + 'px');
    height: v-bind((height-132) + 'px');
    margin-left: 25px;
    margin-top: 15px;
}
.industry-table {
    width: v-bind((width-50) + 'px');
    height: v-bind((height-95) + 'px');
    margin-left: 25px;
    margin-top: 15px;
}
.map {
    width: v-bind((width-480) + 'px');
    height: v-bind((height-95) + 'px');
    display: inline-block;
    margin-left: 50px;
    margin-top: 15px;
    border: 2px solid black !important;
    box-shadow: 3px 6px 10px black !important;
}

</style>