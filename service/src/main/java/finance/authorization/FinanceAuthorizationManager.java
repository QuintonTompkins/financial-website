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

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public class FinanceAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private static final Logger LOGGER = Logger.getLogger( FinanceAuthorizationManager.class.getName() );
    private static final String FAILED_AUTH = "Failed to authorize user";
    private static final String NO_AUTH_TOKEN = "No Auth Token Passed.";

    JwtUtils jwtUtils = new JwtUtils();

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        AuthorizationDecision decision = new AuthorizationDecision(false);
        try{
            if(object.getRequest().getHeader("Authorization") == null){
                LOGGER.log(Level.INFO, NO_AUTH_TOKEN);
            }
            else{
                String jwt = object.getRequest().getHeader("Authorization").replace("Bearer ","");
                jwtUtils.validateToken(jwt, object.getRequest().getRemoteAddr());
                decision = new AuthorizationDecision(true);
            }
        }
        catch(Exception e){
            LOGGER.log(Level.WARNING, FAILED_AUTH, e);
        }
        return decision;
    }
}
