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

public class UserComment {
    private int commentId;
    private Integer userId;
    private String cik;
    private LocalDateTime created;
    private Float minPrice;
    private Float maxPrice;
    private String comment;
    private Integer voteTotal;
    private String userName;

    public UserComment(int commentId, Integer userId, String cik, LocalDateTime created, Float minPrice, Float maxPrice, String comment, Integer voteTotal, String userName) {
        this.commentId = commentId;
        this.userId = userId;
        this.cik = cik;
        this.created = created;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.comment = comment;
        this.voteTotal = voteTotal;
        this.userName = userName;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCik() {
        return cik;
    }

    public void setCik(String cik) {
        this.cik = cik;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Float minPrice) {
        this.minPrice = minPrice;
    }

    public Float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getVoteTotal() {
        return voteTotal;
    }

    public void setVoteTotal(Integer voteTotal) {
        this.voteTotal = voteTotal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}