
SELECT file_name, 
	file_data->'cik' AS cik,
	file_data->'entityName' AS entityName
	FROM finance.company_facts

SELECT file_name, 
	file_data->'facts'->'dei'
	FROM finance.company_facts
	WHERE (file_data->'facts'->'dei') IS NOT NULL;
		 
SELECT file_name, 
	file_data->'facts'->'us-gaap'
	FROM finance.company_facts
	WHERE (file_data->'facts'->'us-gaap') IS NOT NULL;

SELECT file_name,
	file_data->'facts'->'ifrs-full' as ifrs_full
	FROM finance.company_facts
	WHERE (file_data->'facts'->'ifrs-full') IS NOT NULL;
