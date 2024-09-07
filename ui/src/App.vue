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
import { RouterView } from 'vue-router'
import { jwtDecode } from "jwt-decode";
</script>

<template>
  <v-app>
    <div v-if="!isMobileDevice()" class="toolbar">
        <v-btn-toggle color="primary" v-model="path" borderless variant="outlined">
            <v-btn to="/" value="/" class="toolbar-menu-button">Home</v-btn>
            <v-btn to="/search" value="/search" class="toolbar-menu-button">Search</v-btn>
            <v-btn to="/recentFilings" value="/recentFilings" class="toolbar-menu-button">Recent Filings</v-btn>
            <v-btn to="/mapView" value="/mapView" class="toolbar-menu-button">Map View</v-btn>
            <v-btn to="/savedCiks" value="/savedCiks" class="toolbar-menu-button">Saved Ciks</v-btn>
            <v-btn to="/recentComments" value="/recentComments" class="toolbar-menu-button" :disabled="!hasCommentorRole">Recent Comments</v-btn>
            <v-btn to="/account" value="/account" class="toolbar-menu-button">{{jwt != '' ? userName : 'Login'}}</v-btn>
        </v-btn-toggle>
    </div>
    <RouterView @updateJwt="updateJwt" v-slot="{ Component }">
      <component :is="Component" :jwt=jwt />
    </RouterView>
  </v-app>
</template>

<script lang="ts">
export default defineComponent({
    name: 'App',
    components: {
    },
    data() {
      return {
          jwt: "" as string,
          jwtExpiration: 0 as number,
          userName: "" as string,
          width: window.innerWidth,
          height: window.innerHeight,
          path: this.$route.path,
          hasCommentorRole: false as boolean
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
    mounted(){
      const self = this; 
      const savedJwt = localStorage.getItem('jwt');
      if(savedJwt){
        this.updateJwt(savedJwt)
      }
      setInterval(function(){ self.checkForExpiredJwt() }.bind(this), 60000);
      window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
    },
    methods: {
      updateJwt(newJwt : string){
        this.jwt = newJwt
        localStorage.setItem('jwt', this.jwt);
        if(this.jwt == ""){
          this.jwtExpiration = 0
          this.userName = ""
        }
        else{
          const claims: {exp: number, sub: string} = jwtDecode(this.jwt)
          this.jwtExpiration = claims.exp * 1000
          this.userName = claims.sub
        }
      },
      checkForExpiredJwt(){
        if(this.jwt != "" && this.jwtExpiration != 0 && this.jwtExpiration < Date.now()){
          this.updateJwt("")
        }
      },
      isMobileDevice(){
        var hasTouchScreen = false
        if ("maxTouchPoints" in navigator) {
            hasTouchScreen = navigator.maxTouchPoints > 0
        }
        return hasTouchScreen
      }
    }
});
</script>

<style scoped>

.nav-button {
    height: 35px;
    width: 100px;
}

.toolbar {
    width: 100%;
    margin: 0px;
    background-color: #353535;
    border-bottom: 2px solid black;
}

.toolbar-menu-button {
    border: 1px solid black !important;
}
</style>
