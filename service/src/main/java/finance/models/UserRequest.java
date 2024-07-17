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
package finance.models;

import java.time.LocalDateTime;

public class UserRequest {
    private int requestId;
    private int targetId;
    private int userId;
    private String type;
    private String reason;
    private LocalDateTime created;
    private boolean complete;

    public UserRequest() {
        
    }

    public UserRequest(int requestId, int targetId, int userId, String type, String reason, LocalDateTime created, boolean complete) {
        this.requestId = requestId;
        this.targetId = targetId;
        this.userId = userId;
        this.type = type;
        this.reason = reason;
        this.created = created;
        this.complete = complete;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getCommentId() {
        return targetId;
    }

    public void setCommentId(int targetId) {
        this.targetId = targetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getActionTimestamp() {
        return created;
    }

    public void setActionTimestamp(LocalDateTime created) {
        this.created = created;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
