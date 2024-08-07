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

import json
import os
import shutil
import requests
from zipfile import ZipFile
import re
import datetime
from pytz import timezone
import multiprocessing

#Local imports
from Dao import Dao

COMPANY_NAME = "companyfacts"
SUBMISSIONS_NAME = "submissions"
COMPANY_FOLDER_NAME = "./"+COMPANY_NAME
SUBMISSIONS_FOLDER_NAME = "./"+SUBMISSIONS_NAME

def deleteBulkFolder(folderName):
    print("Deleting "+folderName+" json folder")
    if os.path.isdir(folderName):
        shutil.rmtree(folderName)

def downloadBulkFolder(name):
    print("Downloading "+name+" zip")
    zipFileName = "./"+name+".zip"
    folderName = "./"+name
    path = 'bulkdata' if name == 'submissions' else 'xbrl'
    downloadLink = "https://www.sec.gov/Archives/edgar/daily-index/"+path+"/"+name+".zip"
    headers = {
        "User-Agent": os.environ.get('SEC_IDENTIFIER')
    }
    r = requests.get(downloadLink, headers=headers)
    with open(zipFileName, "wb") as outfile:
        outfile.write(r.content)

    print("extracting "+name+" zip")
    with ZipFile(zipFileName, "r") as zip_ref:
        zip_ref.extractall(folderName)

    print("Deleting "+name+" zip")
    if os.path.isfile(zipFileName):
        os.remove(zipFileName)

def parseSubmissionFile(fileName, cik, submissionsJson):
    dao = Dao()
    try:
        dao.openConnection()
        dao.addCompanySummary({
            "cik": cik,
            "name": submissionsJson["name"],
            "sic_description": submissionsJson["sicDescription"],
            "category": submissionsJson["category"],
            "entity_type": submissionsJson["entityType"],
            "street1": submissionsJson["addresses"]["business"]["street1"],
            "street2": submissionsJson["addresses"]["business"]["street2"],
            "city": submissionsJson["addresses"]["business"]["city"],
            "state_country": submissionsJson["addresses"]["business"]["stateOrCountry"],
            "zip_code": submissionsJson["addresses"]["business"]["zipCode"],
            "state_country_description": submissionsJson["addresses"]["business"]["stateOrCountryDescription"]
        })

        exchangeList = []
        for i in range(len(submissionsJson["exchanges"])):
            exchangeList.append({
                "exchange": str(submissionsJson["exchanges"][i]),
                "ticker": str(submissionsJson["tickers"][i])
            })
        dao.addCompanyExchanges(cik, exchangeList)

        filings = []
        for i in range(len(submissionsJson["filings"]["recent"]["accessionNumber"])):
            filings.append("('{0}', '{1}', '{2}', '{3}', '{4}' )".format(cik, 
                                          submissionsJson["filings"]["recent"]["accessionNumber"][i],
                                          submissionsJson["filings"]["recent"]["filingDate"][i],
                                          submissionsJson["filings"]["recent"]["reportDate"][i],
                                          submissionsJson["filings"]["recent"]["form"][i]).replace("''","null")
            )
        if len(filings) != 0:
            dao.addSubmissionFilings(",".join([i for i in filings]))

    except Exception as e:
        print(e)
        dao.addFailedFile(fileName, SUBMISSIONS_NAME, submissionsJson, str(e))
    finally:
        dao.closeConnection()

def parseCompanyFactsFile(fileName, cik, companyfactsJson):
    dao = Dao()
    try:
        dao.openConnection()
        anList = dao.getAccessionNumbersForCompanyFacts(cik)
        if len(anList) != 0:
            companyFacts = [{"accn": i, "data": {}} for i in anList]
            if "facts" in companyfactsJson:
                for section, sValue in companyfactsJson["facts"].items():
                    for dataName, dnValue in sValue.items():
                        for unit, uValue in dnValue["units"].items():
                            for fact in uValue:
                                if fact["accn"] in anList:
                                    next((cf for cf in companyFacts if cf["accn"] == fact["accn"]), None)["data"]["{0}_{1}".format(section,dataName)] = {
                                        "unit": unit,
                                        "value": fact["val"]
                                    }
            for cf in companyFacts:
                dao.updateCompanyFilings(cik,cf["accn"],cf["data"])

    except Exception as e:
        print(e)
        dao.addFailedFile(fileName, COMPANY_NAME, companyfactsJson, str(e))
    finally:
        dao.closeConnection()

def ingestFileData(fileName):
    cik = re.search(r"[0-9]+", fileName).group()
    print("Processing " + cik)
    with open(SUBMISSIONS_FOLDER_NAME+"/"+fileName) as submissionsfile:
        submissionsJson = json.load(submissionsfile)
        parseSubmissionFile(fileName, cik, submissionsJson)

    with open(COMPANY_FOLDER_NAME+"/"+fileName) as companyfactsfile:
        companyfactsJson = json.load(companyfactsfile)
        parseCompanyFactsFile(fileName, cik, companyfactsJson)

def completeBulkIngest(timeDiff):
    dao = Dao()
    try:
        dao.openConnection()
        dao.completeDataCollector(timeDiff)
    except Exception as e:
        print(e)
    finally:
        dao.closeConnection()


def ingestBulkData():
    startTime = datetime.datetime.now()
    folderNames = [COMPANY_NAME,SUBMISSIONS_NAME]

    pool = multiprocessing.Pool(processes=4)
    pool.map(downloadBulkFolder, folderNames)

    comFileList = os.listdir(COMPANY_FOLDER_NAME)
    pool.map(ingestFileData, comFileList)

    pool.map(deleteBulkFolder, folderNames)
    
    timeDiff = datetime.datetime.now() - startTime
    completeBulkIngest(timeDiff.total_seconds())