/*
	Financial Website
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

CREATE SCHEMA finance

CREATE TABLE finance.user (
    user_id INT GENERATED ALWAYS AS IDENTITY,
    user_name VARCHAR UNIQUE NOT NULL,
    user_email VARCHAR UNIQUE NOT NULL,
    user_password VARCHAR UNIQUE NOT NULL,
    PRIMARY KEY(user_id)
);

CREATE TABLE finance.auth_request (
    user_name VARCHAR,
    user_email VARCHAR,
    user_id_found INT,
    origin VARCHAR NOT NULL,
    auth_type VARCHAR NOT NULL,
    date_attempted TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE finance.company_facts (
    file_name VARCHAR,
    file_data JSON,
    updated_date TIMESTAMP
);

CREATE TABLE finance.submissions (
    file_name VARCHAR,
    file_data JSON,
    updated_date TIMESTAMP
);