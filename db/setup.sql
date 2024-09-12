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

-- Create Tables
CREATE TABLE finance.user (
    user_id INT GENERATED ALWAYS AS IDENTITY,
    user_name VARCHAR UNIQUE NOT NULL,
    user_email VARCHAR UNIQUE NOT NULL,
    user_password VARCHAR UNIQUE NOT NULL,
    PRIMARY KEY(user_id)
);

CREATE TABLE finance.user_role (
    user_id INT REFERENCES finance.user(user_id),
    role VARCHAR NOT NULL,
    UNIQUE (user_id, role)
);

CREATE TABLE finance.auth_request (
    user_name VARCHAR,
    user_email VARCHAR,
    user_id_found INT,
    origin VARCHAR NOT NULL,
    auth_type VARCHAR NOT NULL,
    date_attempted TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE finance.data_collector (
    time_taken DECIMAL,
    end_time TIMESTAMP
);

CREATE TABLE finance.failed_file (
    file_name VARCHAR,
    type VARCHAR,
    exception VARCHAR,
    file_data JSON,
    attempted_date TIMESTAMP
);

CREATE TABLE finance.company_summary (
    cik VARCHAR(10),
    name VARCHAR,
    sic_description VARCHAR,
    category VARCHAR,
    entity_type VARCHAR,
    street1 VARCHAR,
    street2 VARCHAR,
    city VARCHAR,
    state_country VARCHAR,
    zip_code VARCHAR,
    state_country_description VARCHAR,
    UNIQUE (cik)
);

CREATE TABLE finance.company_exchange (
    cik VARCHAR(10),
    exchange VARCHAR,
    ticker VARCHAR,
    UNIQUE (cik, exchange)
);

CREATE TABLE finance.company_filing (
    cik VARCHAR(10),
    accession_number VARCHAR,
    filing_date DATE,
    report_date DATE,
    form VARCHAR,
    data JSON,
    UNIQUE (cik, accession_number)
);

CREATE TABLE finance.user_saved_cik (
    user_id INT REFERENCES finance.user(user_id),
    cik VARCHAR(10)
);

CREATE TABLE finance.user_comment (
    comment_id INT GENERATED ALWAYS AS IDENTITY,
    user_id INT REFERENCES finance.user(user_id),
    cik VARCHAR(10),
    created TIMESTAMP DEFAULT NOW(),
    min_price NUMERIC(10,4),
    max_price NUMERIC(10,4),
    comment VARCHAR(10000),
    hidden BOOLEAN DEFAULT FALSE,
    PRIMARY KEY(comment_id)
);

CREATE TABLE finance.user_comment_vote (
    comment_id INT REFERENCES finance.user_comment(comment_id),
    user_id INT REFERENCES finance.user(user_id),
    vote SMALLINT,
    UNIQUE (comment_id, user_id)
);

CREATE TABLE finance.user_request (
    request_id INT GENERATED ALWAYS AS IDENTITY,
    target_id INT,
    user_id INT REFERENCES finance.user(user_id),
    type VARCHAR,
    reason VARCHAR(1000),
    created TIMESTAMP DEFAULT NOW(),
    complete BOOLEAN
);

CREATE TABLE finance.logged_action (
    user_id INT REFERENCES finance.user(user_id),
    affected_id INT,
    action VARCHAR,
    created TIMESTAMP DEFAULT NOW()
);

-- Materialized views

CREATE MATERIALIZED VIEW finance.recent_company_filings_view AS 
    SELECT cf.cik, cf.accession_number, cf.filing_date, cf.report_date, cf.form, cf.data  FROM finance.company_filing cf
    WHERE filing_date > now() - interval '6' YEAR AND cik IN (SELECT cik FROM finance.company_filing WHERE filing_date > NOW() - INTERVAL '1 year' GROUP BY cik);

CREATE UNIQUE INDEX recent_company_filings_view_index ON finance.recent_company_filings_view (cik, accession_number);

CREATE MATERIALIZED VIEW finance.recent_company_filings_data_key AS 
    SELECT d.key, count(*) FROM finance.recent_company_filings_view cf 
        JOIN json_each_text(cf.data) d ON true 
        WHERE cf.data::text <> '{}'::text
        GROUP BY d.key 
        ORDER BY count(*) DESC;

CREATE UNIQUE INDEX recent_company_filings_data_key_index ON finance.recent_company_filings_data_key (key);

CREATE MATERIALIZED VIEW finance.location_data AS 
    SELECT UPPER(state_country) as state_country, sic_description, count(*) AS total_recently_active FROM finance.company_summary 
        WHERE cik IN (SELECT cik FROM finance.recent_company_filings_view GROUP BY cik) 
        GROUP BY UPPER(state_country), sic_description;

CREATE UNIQUE INDEX location_data_index ON finance.location_data (state_country, sic_description);

CREATE MATERIALIZED VIEW finance.recent_company_summary AS 
    SELECT * FROM finance.company_summary 
        WHERE cik IN (SELECT cik FROM finance.recent_company_filings_view GROUP BY cik);

CREATE UNIQUE INDEX recent_company_summary_index ON finance.recent_company_summary (cik);

CREATE OR REPLACE FUNCTION refresh_materialized_views()
RETURNS trigger LANGUAGE plpgsql
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW finance.recent_company_filings_view;
    REFRESH MATERIALIZED VIEW finance.recent_company_filings_data_key;
    REFRESH MATERIALIZED VIEW finance.location_data;
    REFRESH MATERIALIZED VIEW finance.recent_company_summary;
    RETURN NULL;
END $$;

CREATE TRIGGER refresh_materialized_views
AFTER INSERT ON finance.data_collector FOR EACH STATEMENT 
EXECUTE PROCEDURE refresh_materialized_views();