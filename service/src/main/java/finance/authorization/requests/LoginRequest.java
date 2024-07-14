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
package finance.authorization.requests;

public class LoginRequest {
    private String usernameEmail;
    private String password;

    LoginRequest() {}

    LoginRequest(String usernameEmail, String password) {
        this.usernameEmail = usernameEmail;
        this.password = password;
    }
    
    public String getUsernameEmail() {
        return this.usernameEmail;
    }
    
    public void setUsernameEmail(String usernameEmail) {
        this.usernameEmail = usernameEmail;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

}
