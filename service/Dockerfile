FROM maven:3.9.6-eclipse-temurin-22-alpine

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

WORKDIR /
COPY --chown=1000:1000 ./pom.xml pom.xml
COPY --chown=1000:1000 ./src src

RUN mvn -v
CMD ["mvn", "spring-boot:run"]