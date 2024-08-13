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
    <div>
        <div v-if="comment.cik"class="card-text-sub-title">CIK:</div>
        <a v-if="comment.cik" :href="'/company/'+comment.cik" style="display: inline-block;">{{ comment.cik }}</a>
        <div v-if="comment.cik && comment.name" class="card-text-sub-title">Name:</div>
        <div v-if="comment.cik && comment.name" class="card-text" style="display: inline-block;">{{ comment.name }}</div>
        <div class="card-text-sub-title">Username:</div>
        <a :href="'/user/'+comment.userId" style="display: inline-block;">{{ comment.userName }}</a>
        <div class="card-text-sub-title">Created:</div>
        <div class="card-text">{{ comment.created }}</div>
        <div class="card-text-sub-title">Price Range:</div>
        <div class="card-text">${{ comment.minPrice }} to ${{ comment.maxPrice }}</div>
        
        <div class="vote-block">
            <img src="/arrow-sm-up-svgrepo-com.svg" alt="up vote" @click="upVote()"/>
            <div class="card-text" style="font-size: 23px;">{{ comment.voteTotal }}</div>
            <img src="/arrow-sm-down-svgrepo-com.svg" alt="down vote" @click="downVote()"/>
        </div>
        <br>
        <div style="margin: 10px">
            <div class="card-text" style="white-space: pre-line">{{comment.comment}}</div>
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
.vote-block{
    float: right;
    display: inline;
}
</style>