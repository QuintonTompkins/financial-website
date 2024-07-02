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

import os
import time
import requests

#Local imports
from Dao import Dao

def processSubmissions():
    dao = Dao()
    zipFileName = "./companyfacts.zip"
    folderName = "companyfacts"
    time.sleep(5)
    while os.path.isdir(folderName) == False:
        print("waiting for json folder")
        time.sleep(5)
    while os.path.isfile(zipFileName):
        print("waiting for zip file deletion")
        time.sleep(5)
    
    fileNames = os.listdir(folderName)
    for fileName in fileNames:
        print("Getting Submissions for "+fileName)
        downloadLink = 'https://data.sec.gov/submissions/'+fileName
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0',
            'Host': 'data.sec.gov'
        }
        r = requests.get(downloadLink, headers=headers)
        if r.status_code == 200:
            dao.addSubmissions((fileName,r.json()))
        else:
            print("Failed to get submission data. Status: " + str(r.status_code))

