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
import { RouterLink, RouterView } from 'vue-router'
import { jwtDecode } from "jwt-decode";
</script>

<template>
  <div v-if="isMobileDevice()">
    This website is not intended for mobile devices.
  </div>
  <div v-if="!isMobileDevice()">
    <div class="toolbar">
      <router-link to="/"><button class="toolbar-nav-button">Home</button></router-link>
      <router-link to="/search"><button class="toolbar-nav-button">Search</button></router-link>
      <router-link v-if="jwt == ''" to="/account"><button class="toolbar-nav-button">Login</button></router-link>
      <router-link v-if="jwt != ''" to="/account"><button class="toolbar-nav-button">{{ userName }}</button></router-link>
      <div class="toolbar-disclaimer">Nothing on this application should be considered financial advice. Data is provided by the SEC 
        and nothing in the application verifies or validates the information provided. Comments on this application should be assumed
        to be the opinion of the commentor and not financial advice from the application or its owner.</div>
      </div>
    <RouterView @updateJwt="updateJwt" v-slot="{ Component }">
      <component :is="Component" :jwt=jwt />
    </RouterView>
  </div>
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
          userName: "" as string
      };
    },
    mounted(){
      const self = this; 
      const savedJwt = localStorage.getItem('jwt');
      if(savedJwt){
        this.updateJwt(savedJwt)
      }
      setInterval(function(){ self.checkForExpiredJwt() }.bind(this), 60000);
    },
    methods: {
      updateJwt(newJwt : string){
        this.jwt = newJwt
        localStorage.setItem('jwt', this.jwt);
        if(this.jwt == ""){
          this.jwt = ""
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
        if(this.jwtExpiration != 0 && this.jwtExpiration < Date.now()){
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
.toolbar {
  height: 50px; 
  background: #252525;
  border-bottom: solid;
  border-color: black;
}

.toolbar-nav-button {
  height: 35px;
  width: 100px;
  margin: 5px;
}

.toolbar-disclaimer {
  height: 35px;
  width: 1100px;
  margin: 5px;
  display: inline-block;
  color: red;
  font-size: 14px;
}
</style>
