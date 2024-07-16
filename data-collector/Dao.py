"""
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
"""

import psycopg
import os
import json
from enum import Enum
import time

class Dao():
    class RETURN_TYPE(Enum):
        FETCH_ONE = 1
        FETCH_MANY = 2
        FETCH_ALL = 3
        COMMIT = 4

    def __init__(self):
        self.connection = None
        self.db_username = os.environ.get('POSTGRES_USER')
        self.db_password = os.environ.get('POSTGRES_PASSWORD')
        self.db_host = os.environ.get('POSTGRES_HOST')
        self.db_port = os.environ.get('POSTGRES_PORT')
        self.db_name = os.environ.get('POSTGRES_DB')

    def openConnection(self):
        print("Database connecting")
        self.connection = psycopg.connect(
            host=self.db_host,
            port=self.db_port,
            dbname=self.db_name,
            user=self.db_username,
            password=self.db_password,
            keepalives=1,
            keepalives_idle=60,
            keepalives_interval=60,
            keepalives_count=3
        )

    def closeConnection(self):
        print("Database closing connection")
        if self.connection != None and self.connection.closed == 0:
            self.connection.close()

    def rollback(self, returnType):
        if self.connection != None and self.connection.closed == 0 and returnType == self.RETURN_TYPE.COMMIT:
            print("Attempt to roll back commit")
            self.connection.rollback()
        
    def executeQuery(self, query, values, returnType):
        print("Database executing query: " + query[:100])
        print("Database executing values: " + str(values)[:100])
        fetchResult = None
        try:
            with self.connection.cursor() as curs:
                curs.execute(query, values)
                if returnType == self.RETURN_TYPE.FETCH_ONE:
                    fetchResult = curs.fetchone()
                elif returnType == self.RETURN_TYPE.FETCH_MANY:
                    fetchResult = curs.fetchmany()
                elif returnType == self.RETURN_TYPE.FETCH_ALL:
                    fetchResult = curs.fetchall()
                elif returnType == self.RETURN_TYPE.COMMIT:
                    self.connection.commit()
        except Exception as e:
            print(e)
        return fetchResult
    
    def addFailedFile(self, fileName, type, fileData, exception):
        insertQuery = "INSERT INTO finance.failed_file ( file_name, type, exception, file_data, attempted_date ) VALUES ( %s, %s, %s, %s, now() )"
        self.executeQuery(insertQuery, (fileName, type, exception, json.dumps(fileData) ), self.RETURN_TYPE.COMMIT)
    
    def addCompanyExchanges(self, cik, exchangeList):
        for ticker in exchangeList:
            upsertQuery = """INSERT INTO finance.company_exchange ( cik, exchange, ticker ) VALUES ( %s, %s, %s )
                                ON CONFLICT ( cik, exchange ) 
                                DO UPDATE SET ticker = %s """
            
            self.executeQuery(upsertQuery, (# Insert
                                            cik,
                                            ticker["exchange"],
                                            ticker["ticker"],
                                            # update
                                            ticker["ticker"]
                                            ), self.RETURN_TYPE.COMMIT)
        
        exchangesStringList = ""
        if len(exchangeList) > 0:
            exchangesStringList = "AND exchange not in ('"+"','".join([i["exchange"] for i in exchangeList])+"')" 

        deleteQuery = "DELETE FROM finance.company_exchange WHERE cik = %s " + exchangesStringList
        
        self.executeQuery(deleteQuery, ( cik, ), self.RETURN_TYPE.COMMIT)
    
    def addCompanySummary(self, companySummary):
        upsertQuery = """INSERT INTO finance.company_summary (  cik ,
                                                                name ,
                                                                sic_description ,
                                                                category ,
                                                                entity_type ,
                                                                street1 ,
                                                                street2 ,
                                                                city ,
                                                                state_country ,
                                                                zip_code ,
                                                                state_country_description )
                            VALUES ( %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s )
                            ON CONFLICT ( cik ) 
                            DO UPDATE SET   name = %s ,
                                            sic_description = %s ,
                                            category = %s ,
                                            entity_type = %s ,
                                            street1 = %s ,
                                            street2 = %s ,
                                            city = %s ,
                                            state_country = %s ,
                                            zip_code = %s ,
                                            state_country_description = %s"""
        
        self.executeQuery(upsertQuery, (# Insert
                                        companySummary["cik"],
                                        companySummary["name"],
                                        companySummary["sic_description"],
                                        companySummary["category"],
                                        companySummary["entity_type"],
                                        companySummary["street1"],
                                        companySummary["street2"],
                                        companySummary["city"],
                                        companySummary["state_country"],
                                        companySummary["zip_code"],
                                        companySummary["state_country_description"],
                                        # update
                                        companySummary["name"],
                                        companySummary["sic_description"],
                                        companySummary["category"],
                                        companySummary["entity_type"],
                                        companySummary["street1"],
                                        companySummary["street2"],
                                        companySummary["city"],
                                        companySummary["state_country"],
                                        companySummary["zip_code"],
                                        companySummary["state_country_description"]), self.RETURN_TYPE.COMMIT)
    
    def addSubmissionFilings(self, filings):
        insertQuery = """INSERT INTO finance.company_filing (  cik , accession_number , filing_date , report_date , form ) VALUES {0} 
                                ON CONFLICT ( cik , accession_number ) DO NOTHING""".format(filings)
        self.executeQuery(insertQuery, None, self.RETURN_TYPE.COMMIT)
    
    def getAccessionNumbersForCompanyFacts(self, cik):
        anQuery = "SELECT accession_number FROM finance.company_filing WHERE cik = %s AND data IS NULL;"
        anList = self.executeQuery(anQuery, (cik,), self.RETURN_TYPE.FETCH_ALL)
        return [i[0] for i in anList]
    
    def updateCompanyFilings(self, cik, accessionNumber, data):
        updateQuery = "UPDATE finance.company_filing SET data = %s WHERE cik = %s AND accession_number = %s"
        self.executeQuery(updateQuery, (json.dumps(data), cik, accessionNumber), self.RETURN_TYPE.COMMIT)
    
    def completeDataCollector(self, timeDiff):
        insertQuery = "INSERT INTO finance.data_collector ( time_taken, end_time ) VALUES ( %s, now() );"
        self.executeQuery(insertQuery, (timeDiff,), self.RETURN_TYPE.COMMIT)