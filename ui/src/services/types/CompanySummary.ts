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
import type { CompanyTicker } from './CompanyTicker'

export interface CompanySummary {
    cik: string; 
    name?: string;
    sicDescription?: string;
    category?: string;
    entityType?: string;
    street1?: string;
    street2?: string;
    city?: string;
    stateCountry?: string;
    zipCode?: string;
    stateCountryDescription?: string;
    tickers?: CompanyTicker[];
}