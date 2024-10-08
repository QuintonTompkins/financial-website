/*
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
*/
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SearchView from '../views/SearchView.vue'
import AccountView from '../views/AccountView.vue'
import CompanyView from '../views/CompanyView.vue'
import UserView from '../views/UserView.vue'
import RecentFilingView from '@/views/RecentFilingView.vue'
import MapView from '@/views/MapView.vue'
import SavedCiksView from '@/views/SavedCiksView.vue'
import RecentCommentsView from '@/views/RecentCommentsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomeView
    },
    {
      path: '/search',
      name: 'Search',
      component: SearchView
    },
    {
      path: '/recentFilings',
      name: 'Recent Filing',
      component: RecentFilingView
    },
    {
      path: '/mapView',
      name: 'Map View',
      component: MapView
    },
    {
      path: '/savedCiks',
      name: 'Saved Ciks',
      component: SavedCiksView
    },
    {
      path: '/recentComments',
      name: 'Recent Comments',
      component: RecentCommentsView
    },
    {
      path: '/account',
      name: 'Account',
      component: AccountView
    },
    {
      path: '/company/:cik',
      name: 'Company Info',
      component: CompanyView
    },
    {
      path: '/user/:userId',
      name: 'User Info',
      component: UserView
    }
  ]
})

export default router
