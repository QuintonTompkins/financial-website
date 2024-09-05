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

import java.util.List;

public class LocationData {

    private String code;
    private int totalRecentlyActive;
    private List<SicDetails> sicDetails;

    public LocationData(String code, int totalRecentlyActive, List<SicDetails> sicDetails) {
        this.code = code;
        this.totalRecentlyActive = totalRecentlyActive;
        this.sicDetails = sicDetails;
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTotalRecentlyActive() {
        return totalRecentlyActive;
    }

    public void setTotalRecentlyActive(int totalRecentlyActive) {
        this.totalRecentlyActive = totalRecentlyActive;
    }

    public List<SicDetails> getSicDetails() {
        return sicDetails;
    }

    public void setSicDetails(List<SicDetails> sicDetails) {
        this.sicDetails = sicDetails;
    }
}
