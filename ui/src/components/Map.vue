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
import "leaflet/dist/leaflet.css";
import { LMap, LTileLayer, LMarker, LIcon, LPopup } from "@vue-leaflet/vue-leaflet";
import L, { latLngBounds, Icon, type PointExpression } from 'leaflet';
import type { LocationDataLatLng } from '@/services/types/LocationDataExtensions'
</script>

<template>
    <LMap
        ref="map" 
        :zoom="zoom"
        :max-bounds="maxBounds" 
        :center="center">
        <LTileLayer
            url="https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
            layer-type="base"
            name="Esri.WorldImagery"
        ></LTileLayer>
        <template v-for="location in locations">
            <LMarker :lat-lng=[location.latitude,location.longitude] @click="markerClick(location)">
                <LPopup>{{ location.name }}<br>{{ location.totalRecentlyActive }} Companies</LPopup>
            </LMarker>
        </template>
    </LMap>
</template>

<script lang="ts">
export default defineComponent({
    name: 'WarMap',
    props: ["jwt", "locations", "selectLocationCallback"],
    components: {
        LMap,
        LTileLayer,
        LPopup,
        LMarker
    },
    data() {
        return {
            zoom: 2 as number,
            center: [0, 0] as PointExpression,
            maxBounds: latLngBounds([
                [90, 180],
                [-90, -180]
            ]),
        };
    },
    mounted(){
    },
    watch: {
    },
    methods: {
        markerClick(location: LocationDataLatLng){
            this.selectLocationCallback(location)
        }
    }
});
</script>

<style>
</style>