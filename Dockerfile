FROM python:3.12.3-alpine

RUN echo $' \n\
    Financial Website \n\
    Copyright (C) 2024  Quinton Tompkins \n\
    \n\
    This program is free software: you can redistribute it and/or modify \n\
    it under the terms of the GNU General Public License as published by \n\
    the Free Software Foundation, either version 3 of the License, or \n\
    (at your option) any later version. \n\
    \n\
    This program is distributed in the hope that it will be useful, \n\
    but WITHOUT ANY WARRANTY; without even the implied warranty of \n\
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the \n\
    GNU General Public License for more details. \n\
    \n\
    You should have received a copy of the GNU General Public License \n\
    along with this program.  If not, see <https://www.gnu.org/licenses/>.  \n\ '

RUN pip install requests==2.31.0
RUN pip install psycopg-binary==3.1.18
RUN pip install psycopg==3.1.18
RUN pip install jsonschema==4.21.1

WORKDIR /
COPY --chown=1000:1000 ./data-collector/daemon.py daemon.py
COPY --chown=1000:1000 ./data-collector/companyFacts.py companyFacts.py
COPY --chown=1000:1000 ./data-collector/Dao.py Dao.py

CMD ["python","-u","daemon.py"]
