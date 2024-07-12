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

import time
import datetime
from pytz import timezone
import threading

#Local imports
from ingestBulkData import ingestBulkData

if __name__ == '__main__':
    try:
        today = None
        while True:
            # data released nightly at about 3 am EST - https://www.sec.gov/search-filings/edgar-application-programming-interfaces
            if today != datetime.datetime.now(timezone('EST')).day and datetime.datetime.now(timezone('EST')).hour > 0:
                threadBulkIngest = threading.Thread(target=ingestBulkData, args=())
                threadBulkIngest.start()
                threadBulkIngest.join()
                today = datetime.datetime.now(timezone('EST')).day
            print("Main waiting")
            time.sleep(3600)
    finally:
        print('Shutting Down')