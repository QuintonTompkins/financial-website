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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Decoders;

@Component
public class JwtUtils {

    private final int HOURS_FOR_TOKEN_TO_EXPIRE = 12;
    private final int TOKEN_LIFE_MS = HOURS_FOR_TOKEN_TO_EXPIRE * 60 * 60 * 1000;

    public String createToken(Map<String, Object> claims, String userName, int userId, String encryptedPassword) {
        SecretKey key = getSigningKey(encryptedPassword);
        return Jwts.builder()
            .header()
            .keyId(Integer.toString(userId))
            .and()
            .claims(claims)
            .subject(userName)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + TOKEN_LIFE_MS))
            .signWith(key)
            .compact();
    }

    public SecretKey getSigningKey(String key) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key.replaceAll("[^0-9a-zA-Z]","0")));
    }

    public Claims validateToken(String authToken, String ip) {
        try{
            Claims claims = Jwts.parser().keyLocator(new UserIdLocator()).build().parseSignedClaims(authToken).getPayload();
            if(!claims.get("ip").equals(ip)){
                throw new AuthorizationServiceException("invalid ip");
            }
            return claims;
        }
        catch(ExpiredJwtException e){
            throw e;
        }
        catch(Exception e){
            throw new AuthorizationServiceException("Invalid authorization header provided.");
        }
    }

    public int getUserId(String authToken) {
        try{
            Claims claims = Jwts.parser().keyLocator(new UserIdLocator()).build().parseSignedClaims(authToken).getPayload();
            return (int) claims.get("userId");
        }
        catch(ExpiredJwtException e){
            throw e;
        }
        catch(Exception e){
            throw new AuthorizationServiceException("Invalid authorization header provided.");
        }
    }

    public List<String> getUserRoles(String authToken) {
        try{
            Claims claims = Jwts.parser().keyLocator(new UserIdLocator()).build().parseSignedClaims(authToken).getPayload();
            return (List<String>) claims.get("roles");
        }
        catch(ExpiredJwtException e){
            throw e;
        }
        catch(Exception e){
            throw new AuthorizationServiceException("Invalid authorization header provided.");
        }
    }

    public void checkForValidRole(String authToken, List<String> rolesToFind) {
        List<String> roles = getUserRoles(authToken);
        for (String role : roles) {
            if (rolesToFind.contains(role)) {
                return ;
            }
        }
        throw new AuthorizationServiceException("User does not have a valid role for this action.");
    }

    public void checkForValidRole(String authToken, String roleToFind) {
        List<String> roles = getUserRoles(authToken);
        if(roles.contains(roleToFind)){
            return ;
        }
        throw new AuthorizationServiceException("User does not have a valid role for this action.");
    }
}
