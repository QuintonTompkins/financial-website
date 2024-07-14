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
package finance.authorization;

import java.security.Key;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.LocatorAdapter;

public class UserIdLocator extends LocatorAdapter<Key> {

    @Override
    public Key locate(JwsHeader header) {
        int userId = Integer.parseInt(header.getKeyId());
        AuthDao authDao = new AuthDao();
        JwtUtils jwtUtils = new JwtUtils();
        User user = authDao.getUser(userId);
        return jwtUtils.getSigningKey(user.getPassword()); 
    }

}
