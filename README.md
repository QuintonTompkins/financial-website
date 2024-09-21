# financial-website

The goal of this application is to provide the following.

1. The application should support value investing goals and the evaluation of company fundamentals.
2. The application should encourage user engagement in value investing while avoiding low-effort interactions.

# Disclaimer

This application shows financial information, but nothing on this application should be considered financial advice. Data is provided by the SEC and nothing in the application verifies or validates the information provided. Comments on this application should be assumed to be the opinion of the commentor and not financial advice from the application or its owner.

# Database

A set up file is provided that should be run on the database of your choosing prior to running the services. Some materialized views are used to filter out filings that are unlikely to of any use on this application.

# data-collector

The data collector pulls company data from the SEC and imports it into your database. It does this by using two bulk files for submissions and companyfacts. It needs to update submissions each time as they can change, but companyfacts are expected to be static and it will only look for new ones mentioned in the submissions. In the database the submissions are used to populate the company summary, and the company facts are used to populate the filings. After all the new data is ingested, the data collector will add an entry recording the time it took to update and this will trigger the materialized views to update.

How to run: Make sure the '.env' file is up to date and contains your database values. This container contains a loop that collects data once per day after 4 am EST. The first time it runs it will take over a day to complete as it backfills data. After that a normal run should take less than an hour.

'docker compose up --build data-collector'

# service

The service layer is made using Spring and GraphQL. After running the service, a developer should be able to access a GraphQL user interface by appending '/graphiql'. Some api calls are restricted and use JWTS for authentication. The JWTS are signed with the encrypted version of user passwords, and if the user updates their password all previous sessions will be made invalid.

How to run: Make sure the '.env' file is up to date and contains your database values, and email information if needed.

'docker compose up --build service'

# User Interface

How to run: to run with the local back end run 'npm run dev' in the ui folder. The public production service is also available and accessible if the correct .env file is used. A built version currently deployed is included in the dist folder which points to the production service.