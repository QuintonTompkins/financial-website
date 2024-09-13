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

import * as UserApi from '@/services/UserApi.js'
import * as FinanceApi from '@/services/FinanceApi.js'
import type { UserCommentWithName } from '@/services/types/UserCommentExtensions';
import UserComment from '@/components/UserComment.vue'
</script>

<template>
    <div class="scrollable-list">
        <div v-for="comment in comments">
            <v-card class="card-width">
                <UserComment :comment="comment" :jwt="jwt" />
            </v-card>
        </div>
    </div>
</template>

<script lang="ts">
export default defineComponent({
    name: 'UserView',
    props: ["jwt"],
    components: {
    },
    data() {
        return {
            userId: +this.$route.params.userId as number,
            width: window.innerWidth,
            height: window.innerHeight,
            comments: [] as UserCommentWithName[],
        };
    },
    watch: {
        jwt: {
            immediate: true, 
            handler (newVal, oldVal) {
                if(newVal == ""){
                    this.$router.push(`/`)
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
            const responseFilings = await UserApi.getUserComments(this.jwt, this.userId)
            this.comments = responseFilings.data.data.userComments
            await this.getCompanyNames(this.comments)
        },
        async getCompanyNames(records: any[]){
            for(let record of records){
                const nameResponse = await FinanceApi.getSmallCompanySummary(record.cik)
                record.name = nameResponse.data.data.companySummaries[0].name
            }
        },
    }
});
</script>

<style scoped>
.scrollable-list {
  height: v-bind((height-60) + 'px');
  overflow-y: auto;
}
.card-width{
    width: v-bind((width-30) + 'px');
}
</style>