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
import type { UserComment } from '@/services/types/UserComment';
</script>

<template>
    <div style="margin: 10px;">
        <div class="left-block">
            <div class="card-text-sub-title">Username:</div>
            <v-btn :to="'/user/'+comment.userId" color="secondary" variant="text" density="compact" style="display: inline-block; margin-top: 3px;">{{ comment.userName }}</v-btn>
            <div class="card-text-sub-title">Created:</div>
            <div class="card-text">{{ comment.created }}</div>
            <br>
            <div v-if="comment.cik"class="card-text-sub-title">CIK:</div>
            <v-btn v-if="comment.cik" :to="'/company/'+comment.cik" color="secondary" variant="text" density="compact" style="display: inline-block; margin-top: 3px;">{{ comment.cik }}</v-btn>
            <div v-if="comment.cik && comment.name" class="card-text-sub-title">Name:</div>
            <div v-if="comment.cik && comment.name" class="card-text" style="display: inline-block;">{{ comment.name }}</div>
            <br v-if="comment.cik && comment.name">
            <div v-if="comment.minPrice >= 0 || comment.maxPrice >= 0" class="card-text-sub-title">Price Range:</div>
            <div v-if="comment.minPrice >= 0 || comment.maxPrice >= 0" class="card-text">
                {{ comment.minPrice < 0 ? '-' : '$' + comment.minPrice }} to {{ comment.maxPrice < 0 ? '-' : '$' + comment.maxPrice }}
            </div>
            <br v-if="comment.minPrice >= 0 || comment.maxPrice >= 0">
            <div class="card-text" style="white-space: pre-line">{{comment.comment}}</div>
        </div>
        <div class="vote-block">
            <v-btn :icon="'mdi-arrow-up'"
                            :color="'white'" 
                            size="small" 
                            variant="text" 
                            density="compact"
                            @click="upVote"/>
            <div class="card-text" style="display: block;">{{ comment.voteTotal }}</div>
            <v-btn :icon="'mdi-arrow-down'"
                            :color="'white'" 
                            size="small" 
                            variant="text" 
                            density="compact"
                            @click="downVote"/>
        </div>
    </div>
</template>

<script lang="ts">
export default defineComponent({
    name: 'UserComment',
    props: ["jwt","comment"],
    components: {
    },
    data() {
        return {
        }
    },
    mounted() {
    },

    methods: {
        goToCompanyPage(cik?: String){
            this.$router.push(`/company/${this.comment.cik}`)
        },
        goToUserPage(userId?: number){
            this.$router.push(`/user/${this.comment.userId}`)
        },
        upVote(){
            UserApi.upVoteComment(this.jwt, this.comment.commentId).then((response: { data: { data: String }; status: number; }) => {
                UserApi.getCommentVotes(this.jwt, this.comment.commentId).then((response: { data: { data: { userComments: UserComment[] } }; status: number; }) => {
                    this.comment.voteTotal = response.data.data.userComments[0].voteTotal
                })
            })
        },
        downVote(){
            UserApi.downVoteComment(this.jwt, this.comment.commentId).then((response: { data: { data: String }; status: number; }) => {
                UserApi.getCommentVotes(this.jwt, this.comment.commentId).then((response: { data: { data: { userComments: UserComment[] } }; status: number; }) => {
                    this.comment.voteTotal = response.data.data.userComments[0].voteTotal
                })
            })
        }
    }
});
</script>

<style scoped>
.left-block{
    width: calc(100% - 40px);
    display: inline-block;
    margin-left: 10px
}
.vote-block{
    width: 20px;
    float: right;
    vertical-align: top;
}
</style>