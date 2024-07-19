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
  <div class="toolbar">
    <router-link to="/"><button class="toolbar-nav-button">Home</button></router-link>
    <router-link to="/search"><button class="toolbar-nav-button">Search</button></router-link>
    <router-link to="/company"><button class="toolbar-nav-button">Company</button></router-link>
    <router-link v-if="jwt == ''" to="/account"><button class="toolbar-nav-button">Login</button></router-link>
    <router-link v-if="jwt != ''" to="/account"><button class="toolbar-nav-button">{{ userName }}</button></router-link>
  </div>
  <RouterView @updateJwt="updateJwt" v-slot="{ Component }">
    <component :is="Component" :jwt=jwt />
  </RouterView>
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
      setInterval(function(){ self.checkForExpiredJwt() }.bind(this), 60000);
    },
    methods: {
      updateJwt(newJwt : string){
        window.localStorage.setItem('jwt', newJwt)
        this.jwt = newJwt
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
      }
    }
});
</script>

<style scoped>
.toolbar {
  height: 50px; 
  background: #0f3d3d;
  border-bottom: solid;
  border-color: black;
}

.toolbar-nav-button {
  height: 35px;
  width: 100px;
  margin: 5px;
}

.toolbar-username {
  height: 35px;
  width: 100px;
  margin: 5px;
  display: inline;
}
</style>
