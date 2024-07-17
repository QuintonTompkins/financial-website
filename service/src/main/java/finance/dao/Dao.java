/* 
    Finance Website
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
*/
package finance.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.RuntimeException;

public class Dao {
    protected static final Logger LOGGER = Logger.getLogger(Dao.class.getName());
    protected static final String url = "jdbc:postgresql://" + System.getenv("POSTGRES_HOST") + ":" 
                                            + System.getenv("POSTGRES_PORT") + "/" + System.getenv("POSTGRES_DB");
    protected static final String user = System.getenv("POSTGRES_USER");
    protected static final String password = System.getenv("POSTGRES_PASSWORD");
    
    protected static final List<String> NUMERIC_COLUMN_LIST = Arrays.asList();
    protected static final List<String> STRING_COLUMN_LIST = Arrays.asList();
    protected static final List<String> DATE_COLUMN_LIST = Arrays.asList();
    protected static final List<String> BOOLEAN_COLUMN_LIST = Arrays.asList();

    protected Connection connection = null;

    public Dao() {
        getConnection(url, user, password);
    }
    
    public void getConnection(String url, String user, String password) {
        try{
            if (this.connection == null || !this.connection.isValid(5) || this.connection.isClosed()) {
                try {
                    this.connection = DriverManager.getConnection(url, user, password);
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
        catch(Exception ex){
            throw new RuntimeException("DB connection failure");
        }
    }

}
