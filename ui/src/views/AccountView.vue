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
import * as AuthorizationApi from '@/services/AuthorizationApi.js'
</script>

<template>
    <div class="scrollable-list" style="margin: 15px;">
        <div v-if="jwt == ''">
            <h4>Login</h4>
            <input type="text" placeholder="Username/Email" v-model="usernameEmail"></input><br>
            <input type="text" placeholder="Password" v-model="passwordLogin"></input><br>
            <button @click="login">login</button><br>
            <h4>Create User</h4>
            <input type="text" placeholder="Username" v-model="username"></input><br>
            <input type="text" placeholder="Email" v-model="email"></input><br>
            <input type="text" placeholder="Password" v-model="passwordCreate"></input><br>
            <button @click="createUser">create user</button><br>
            <h4>Reset Password</h4>
            <input type="text" placeholder="Email" v-model="emailReset"></input><br>
            <button @click="resetPassword">reset password</button>
        </div>
        <div v-if="jwt != ''">
            <h4>Update Password</h4>
            <input type="text" placeholder="Email" v-model="emailPassword"></input><br>
            <input type="text" placeholder="Old Password" v-model="oldPassword"></input><br>
            <input type="text" placeholder="New Password" v-model="newPassword1"></input><br>
            <input type="text" placeholder="New Password again" v-model="newPassword2"></input><br>
            <button :disabled="newPassword1 != newPassword2" @click="updatePassword">update password</button><br>
            <h4>Update Username</h4>
            <input type="text" placeholder="Username" v-model="newUsername"></input><br>
            <button @click="updateUsername">update username</button>
            <h4>Update Email</h4>
            <input type="text" placeholder="Email" v-model="newEmail"></input><br>
            <button @click="updateEmail">update email</button>
            <h4>Request Commentor Status</h4>
            <input type="text" placeholder="Reason" v-model="reason" style="width: 500px; height: 50px;"></input><br>
            <button @click="requestCommentor">request commentor status</button>
            <h4>Report Data/Website Issue</h4>
            <input type="text" placeholder="Reason" v-model="reason" style="width: 500px; height: 50px;"></input><br>
            <button @click="reportIssue">report issue</button>
            <h4>Logout</h4>
            <button @click="logout">Logout</button>
        </div>
    </div>
</template>

<script lang="ts">
export default defineComponent({
    name: 'AccountView',
    emits: ['updateJwt'],
    props: ["jwt"],
    components: {
    },
    data() {
        return {
            usernameEmail: "" as string,
            username: "" as string,
            email: "" as string,
            emailReset: "" as string,
            emailPassword: "" as string,
            password: "" as string,
            passwordLogin: "" as string,
            passwordCreate: "" as string,
            oldPassword: "" as string,
            newPassword1: "" as string,
            newPassword2: "" as string,
            newUsername: "" as string,
            newEmail: "" as string,
            reason: "" as string,
            width: window.innerWidth,
            height: window.innerHeight
        };
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })  
    },
    methods: {
        createUser(){
            AuthorizationApi.createUser(this.username, this.email, this.passwordCreate).then((response: { data: { data: { createUser: String } }; status: number; }) => {
                if(response.data.data.createUser != null)
                    this.$emit('updateJwt',response.data.data.createUser)
            })
            this.username = ""
            this.email = ""
            this.passwordCreate = ""
        },
        login(){
            AuthorizationApi.login(this.usernameEmail, this.passwordLogin).then((response: { data: { data: { login: String } }; status: number; }) => {
                if(response.data.data.login != null)
                    this.$emit('updateJwt',response.data.data.login)
            })
            this.usernameEmail = ""
            this.passwordLogin = ""
        },
        updatePassword(){
            AuthorizationApi.updatePassword(this.emailPassword, this.oldPassword, this.newPassword1).then((response: { data: { data: { updatePassword: String } }; status: number; }) => {
                if(response.data.data.updatePassword != null)
                    this.$emit('updateJwt',"")
            })
            this.emailPassword = ""
            this.oldPassword = ""
            this.newPassword1 = ""
            this.newPassword2 = ""
        },
        resetPassword(){
            AuthorizationApi.resetPassword(this.emailReset).then((response: { data: { data: { resetPassword: String } }; status: number; }) => {
                if(response.data.data.resetPassword != null)
                    this.$emit('updateJwt',"")
            })
            this.emailReset = ""
        },
        updateUsername(){
            AuthorizationApi.updateUsername(this.newUsername,this.jwt).then((response: { data: { data: { updateUserName: String } }; status: number; }) => {
                if(response.data.data.updateUserName != null)
                    this.$emit('updateJwt',response.data.data.updateUserName)
            })
            this.newUsername = ""
        },
        updateEmail(){
            AuthorizationApi.updateEmail(this.newEmail,this.jwt).then((response: { status: number; }) => {})
            this.newEmail = ""
        },
        requestCommentor(){
            AuthorizationApi.requestCommentorStatus(this.reason,this.jwt).then((response: { status: number; }) => {})
            this.reason = ""
        },
        reportIssue(){
            AuthorizationApi.reportIssue(this.reason,this.jwt).then((response: { status: number; }) => {})
            this.reason = ""
        },
        logout(){
            this.$emit('updateJwt',"")
        }
    }
});
</script>

<style scoped>
.scrollable-list {
  height: v-bind((height-75) + 'px');
  overflow-y: auto;
}
</style>