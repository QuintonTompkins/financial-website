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
    <div class="scrollable-list" style="margin: 15px;">
        <v-card style="margin: 20px;">
            <div style="margin: 15px;">
                <div v-if="isMobileDevice()">This website is not intended for mobile devices or touch screens. Please switch devices to view.</div>
                <h1 v-if="!isMobileDevice()">Welcome to Quinton's Financial Website!</h1>
                <div v-if="!isMobileDevice()">
                    Besides having one of the most creative website names ever, you may also find some useful utilities on this site. This website was created with the following goals in mind:
                    <br>
                    <br>
                    1. The application should support value investing goals and the evaluation of company fundamentals.
                    <br>
                    2. The application should encourage user engagement in value investing while avoiding low-effort interactions.
                    <br>
                    <h2>Valuable Information</h2>
                    All data is provided by the SEC and originates from company filings. Companies may occasionally file incorrect data and later correct it. 
                    It is important to use multiple tools (including your own judgment) to evaluate companies.
                    <br>
                    <br>
                    The website aims to be a place where a small group of individuals share information and expertise, providing a net benefit to all commenting users when investing. 
                    To accomplish this, we must highly moderate to ensure high quality and no freeloaders.
                    <br>
                    <br>
                    All users are allowed to use basic functionality to screen and view company information on this site. All users can report issues with the website or data.
                    Users must request the ability to comment on the website. Moderators will screen users to ensure they can provide quality reports on companies. Low-quality 
                    comments or a lack of comments may result in commenting abilities being revoked.
                    <h2>Investing Strategies</h2>
                    We do not recommend any single investing strategy on this site. We only try to evaluate an accurate price for a stock and share that information with the group.
                    Each user should evaluate their own risk tolerance and should have a strong understanding of the investing strategies they are using. 
                    If they do not, then they should hire a financial manager instead of using this site.
                    <br>
                    <br>
                    We recommend that users keep the following ideas in mind when investing.
                    <br>
                    <br>
                    1. Accurate value investing will be an accurate measure of stock value in the long term, but anything can happen in the short term.
                    <br>
                    2. An overpriced stock (especially over hyped meme stocks) will continue to behave irrationally and value investing will be of little to no use in the short term.
                    <br>
                    3. Buying stock at a fair or cheap price works well in most situations. The difficulty is figuring out what a fair price is.
                    <br>
                    4. If a stock bounces between a good price and a fair price, it can be very profitable to open and close covered call positions during the price fluctuations.
                    <br>
                    5. Emotional attachment to an investment will lead to bad investment actions. If you feel emotional distress when the stock moves againest you, 
                    that means your position was too large and you are risking too much.
                    <br>
                    6. Understanding stock options can be highly lucrative and risky. Users should educate themselves on how options work at a minimum.
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