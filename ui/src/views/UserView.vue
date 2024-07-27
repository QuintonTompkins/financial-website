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
import type { UserComment } from '@/services/types/UserComment'
</script>

<template>
    <div> TO DO
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
            userId: this.$route.params.userId as String,
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
    }
});
</script>

<style scoped>

</style>