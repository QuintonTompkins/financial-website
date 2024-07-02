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

#Local imports
from Dao import Dao

def processSubmissions():
    zipFileName = "./submissions.zip"
    folderName = "./submissions"

    print("Downloading zip")
    downloadLink = 'https://www.sec.gov/Archives/edgar/daily-index/bulkdata/submissions.zip'
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0',
        'Host': 'www.sec.gov'
    }
    r = requests.get(downloadLink, headers=headers)
    with open(zipFileName, 'wb') as outfile:
        outfile.write(r.content)

    print("extracting zip")
    with ZipFile(zipFileName, 'r') as zip_ref:
        zip_ref.extractall(folderName)

    print("Deleting zip")
    if os.path.isfile(zipFileName):
        os.remove(zipFileName)

    print("Inserting data into database")
    dao = Dao()
    fileList = os.listdir(folderName)
    for fileName in fileList:
        with open(folderName+"/"+fileName) as file:
            fileJson = json.load(file)
            dao.addSubmissions((fileName,fileJson))

    print("Deleting json folder")
    if os.path.isdir(folderName):
        shutil.rmtree(folderName)