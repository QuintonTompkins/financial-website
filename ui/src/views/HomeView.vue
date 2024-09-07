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
</script>

<template>
    <div class="scrollable-list" style="margin-left: 15px; margin-right: 15px;">
        <v-card>
            <div style="margin: 15px;">
                <h1>Welcome to Quinton's Finance Website!</h1>
                <div v-if="isMobileDevice()">This website is not intended for mobile devices or touch screens. Please switch devices to view.</div>
                <div v-if="!isMobileDevice()">
                    Besides having one of the most creative website names ever, you may also find some useful utilities on this site. This website was created with the following goals in mind:
                    <br>
                    <br>
                    1. The application should support value investing goals and the evaluation of company fundamentals.
                    <br>
                    2. The application should encourage user engagement in value investing while avoiding low-effort interactions.
                    <br>
                    <br>
                    <h2>Disclaimer</h2>
                    This application shows financial information, but nothing on this application should be considered financial advice. 
                    Data is provided by the SEC and nothing in the application verifies or validates the information provided. Comments on 
                    this application should be assumed to be the opinion of the commentor and not financial advice from the application or its owner.
                    <br>
                    <br>
                    <h2>Valuable Information</h2>
                    All data is provided by the SEC and originates from company filings. Companies may occasionally file incorrect data and later correct it. 
                    It is important to use multiple tools (including your own judgment) to evaluate companies.
                    <br>
                    <br>
                    The website aims to be a place where a small group of individuals share information and expertise, providing a net benefit to all commenting users when investing. 
                    To accomplish this, we must highly moderate to ensure high quality assessments.
                    <br>
                    <br>
                    All site visiters are allowed to use basic functionality to screen and view company information on this site. All users can report issues with the website or data.
                    <br>
                    <br>
                    Users must request the ability to comment or view comments on the website. Moderators will screen users to ensure they can provide quality assessments on companies.
                    Low-quality comments or a lack of comments may result in commenting abilities being revoked.
                    <br>
                    <br>
                    <a :href="`https://github.com/QuintonTompkins/financial-website`">Source code for this website can be viewed here.</a>
                </div>
            </div>
        </v-card>
    </div>
</template>

<script lang="ts">
export default defineComponent({
    name: 'HomeView',
    props: ["jwt"],
    components: {
    },
    data() {
        return {
            width: window.innerWidth,
            height: window.innerHeight,
        };
    },
    mounted() {
        window.addEventListener('resize', () => {
            this.width = window.innerWidth
            this.height = window.innerHeight
        })
    },
    methods: {
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
.scrollable-list {
  height: v-bind((height-80) + 'px');
  overflow-y: auto;
}
</style>