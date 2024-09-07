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
    <div class="scrollable-list" style="margin-left: 15px; margin-right: 15px; margin-top: 20px;">
        <div v-if="jwt == ''">
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Login</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-form @submit.prevent>
                            <v-text-field type="text" placeholder="Username/Email" v-model="usernameEmail"></v-text-field><br>
                            <v-text-field placeholder="Password" 
                                            v-model="passwordLogin" 
                                            :append-icon="showPasswordLogin ? 'mdi-eye' : 'mdi-eye-off'"
                                            :type="showPasswordLogin ? 'text' : 'password'" 
                                            @click:append="showPasswordLogin = !showPasswordLogin"></v-text-field><br>
                            <v-btn type="submit" color="primary" @click="login">login</v-btn><br>
                        </v-form>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Create User</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-form @submit.prevent>
                            <v-text-field type="text" placeholder="Username" v-model="username"></v-text-field><br>
                            <v-text-field type="text" placeholder="Email" v-model="email"></v-text-field><br>
                            <v-text-field placeholder="Password" 
                                            v-model="passwordCreate"
                                            :type="showPasswordCreate ? 'text' : 'password'" 
                                            :append-icon="showPasswordCreate ? 'mdi-eye' : 'mdi-eye-off'"
                                            @click:append="showPasswordCreate = !showPasswordCreate"></v-text-field><br>
                            <v-btn type="submit" color="primary" @click="createUser">create user</v-btn><br>  
                        </v-form>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Reset Password</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-form @submit.prevent>
                            <v-text-field type="text" placeholder="Email" v-model="emailReset"></v-text-field><br>
                            <v-btn type="submit" color="primary" @click="resetPassword">reset password</v-btn>
                        </v-form>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
        </div>
        <div v-if="jwt != ''">
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Update Password</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-form @submit.prevent>
                            <v-text-field type="text" placeholder="Email" v-model="emailPassword"></v-text-field><br>
                            <v-text-field placeholder="Old Password" 
                                            v-model="oldPassword"
                                            :type="showOldPassword ? 'text' : 'password'" 
                                            :append-icon="showOldPassword ? 'mdi-eye' : 'mdi-eye-off'"
                                            @click:append="showOldPassword = !showOldPassword"></v-text-field><br>
                            <v-text-field placeholder="New Password" 
                                            v-model="newPassword1"
                                            :type="showNewPassword1 ? 'text' : 'password'" 
                                            :append-icon="showNewPassword1 ? 'mdi-eye' : 'mdi-eye-off'"
                                            @click:append="showNewPassword1 = !showNewPassword1"></v-text-field><br>
                            <v-text-field placeholder="New Password again" 
                                            v-model="newPassword2"
                                            :type="showNewPassword2 ? 'text' : 'password'" 
                                            :append-icon="showNewPassword2 ? 'mdi-eye' : 'mdi-eye-off'"
                                            @click:append="showNewPassword2 = !showNewPassword2"></v-text-field><br>
                            <v-btn type="submit" color="primary" :disabled="newPassword1 != newPassword2" @click="updatePassword">update password</v-btn><br>
                        </v-form>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Update Username</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-form @submit.prevent>
                            <v-text-field type="text" placeholder="Username" v-model="newUsername"></v-text-field><br>
                            <v-btn type="submit" color="primary" @click="updateUsername">update username</v-btn>
                        </v-form>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Update Email</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-form @submit.prevent>
                            <v-text-field type="text" placeholder="Email" v-model="newEmail"></v-text-field><br>
                            <v-btn type="submit" color="primary" @click="updateEmail">update email</v-btn>
                        </v-form>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Request Commentor Status</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-form @submit.prevent>
                            <v-text-field type="text" placeholder="Reason" v-model="reasonStatus"></v-text-field><br>
                            <v-btn type="submit" color="primary" @click="requestCommentor">request commentor status</v-btn>
                        </v-form>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Report Data/Website Issue</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-form @submit.prevent>
                            <v-text-field type="text" placeholder="Reason" v-model="reasonIssue"></v-text-field><br>
                            <v-btn type="submit" color="primary" @click="reportIssue">report issue</v-btn>
                        </v-form>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
            <v-expansion-panels>
                <v-expansion-panel>
                    <v-expansion-panel-title>Logout</v-expansion-panel-title>
                    <v-expansion-panel-text>
                        <v-btn color="primary" @click="logout">Logout</v-btn>
                    </v-expansion-panel-text>
                </v-expansion-panel>
            </v-expansion-panels>
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
            showEmailPassword: false as boolean,
            password: "" as string,
            showPassword: false as boolean,
            passwordLogin: "" as string,
            showPasswordLogin: false as boolean,
            passwordCreate: "" as string,
            showPasswordCreate: false as boolean,
            oldPassword: "" as string,
            showOldPassword: false as boolean,
            newPassword1: "" as string,
            showNewPassword1: false as boolean,
            newPassword2: "" as string,
            showNewPassword2: false as boolean,
            newUsername: "" as string,
            newEmail: "" as string,
            reasonStatus: "" as string,
            reasonIssue: "" as string,
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
            AuthorizationApi.requestCommentorStatus(this.reasonStatus,this.jwt).then((response: { status: number; }) => {})
            this.reasonStatus = ""
        },
        reportIssue(){
            AuthorizationApi.reportIssue(this.reasonIssue,this.jwt).then((response: { status: number; }) => {})
            this.reasonIssue = ""
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