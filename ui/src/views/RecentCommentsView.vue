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
import type { UserCommentWithName } from '@/services/types/UserCommentExtensions'
import UserComment from '@/components/UserComment.vue'
</script>

<template>
    <div style="margin-left: 15px; margin-top: 10px">
        <v-progress-linear v-if="loading" color="primary" indeterminate class="loader"></v-progress-linear>
    </div>
    <div v-if="!loading">
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
            comments: [] as UserCommentWithName[],
            width: window.innerWidth,
            height: window.innerHeight,
            loading: false as Boolean
        };
    },
    watch: {
        jwt: {
            immediate: true, 
            handler (newVal, oldVal) {
                if(newVal !== ""){
                    const claims: {exp: number, sub: string, roles: String[]} = jwtDecode(newVal)
                    if(!claims.roles.includes('commentor')){
                        this.$router.push("/account")
                    }
                }
            }
        }
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
        this.getComments()
    },

    methods: {
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
                const nameResponse = await FinanceApi.getSmallCompanySummary(record.cik)
                record.name = nameResponse.data.data.companySummaries[0].name
            }
            return records
        }
    }
});
</script>

<style scoped>
.scrollable-list {
    height: v-bind((height-75) + 'px');
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