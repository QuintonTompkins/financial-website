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
            password=self.db_password)

    def closeConnection(self):
        print("Database closing connection")
        if self.connection != None:
            self.connection.close()
        
    def executeQuery(self, query, values, returnType):
        print("Database executing query: " + query)
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
            self.connection.rollback()
        return fetchResult
    
    def addCompanyfacts(self, file):
        try:
            self.openConnection()
            existenceCheckQuery = "SELECT COUNT(*) FROM finance.company_facts WHERE file_name = %s;"
            insertQuery = "INSERT INTO finance.company_facts ( file_name, file_data, updated_date ) VALUES ( %s, %s, now() );"
            updateQuery = "UPDATE finance.company_facts SET file_data = %s , updated_date = now() WHERE file_name = %s;"
            count = self.executeQuery(existenceCheckQuery, (file[0],), self.RETURN_TYPE.FETCH_ONE)[0]
            if count == 1:
                self.executeQuery(updateQuery, (json.dumps(file[1]), file[0]), self.RETURN_TYPE.COMMIT)
            else:
                self.executeQuery(insertQuery, (file[0], json.dumps(file[1])), self.RETURN_TYPE.COMMIT)
        except Exception as e:
            print(e)
        finally:
            self.closeConnection()
    
    def getCompanyFactsFileNames(self):
        fileNames = []
        try:
            self.openConnection()
            fileNameQuery = "SELECT file_name FROM finance.company_facts;"
            fileNames = self.executeQuery(fileNameQuery, None, self.RETURN_TYPE.FETCH_ALL)
        except Exception as e:
            print(e)
        finally:
            self.closeConnection()
        return fileNames
    
    def addSubmissions(self, file):
        try:
            self.openConnection()
            existenceCheckQuery = "SELECT COUNT(*) FROM finance.submissions WHERE file_name = %s;"
            insertQuery = "INSERT INTO finance.submissions ( file_name, file_data, updated_date ) VALUES ( %s, %s, now() );"
            updateQuery = "UPDATE finance.submissions SET file_data = %s , updated_date = now() WHERE file_name = %s;"
            count = self.executeQuery(existenceCheckQuery, (file[0],), self.RETURN_TYPE.FETCH_ONE)[0]
            if count == 1:
                self.executeQuery(updateQuery, (json.dumps(file[1]), file[0]), self.RETURN_TYPE.COMMIT)
            else:
                self.executeQuery(insertQuery, (file[0], json.dumps(file[1])), self.RETURN_TYPE.COMMIT)
        except Exception as e:
            print(e)
        finally:
            self.closeConnection()