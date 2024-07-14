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

public class CompanyFiling {
    private String cik;
    private String accessionNumber;
    private String filingDate;
    private String reportDate;
    private String form;
    private Object data;
    
    public CompanyFiling() {
    
    }
    public CompanyFiling(String cik, String accessionNumber, String filingDate, String reportDate, String form, Object data) {
      this();
      this.cik = cik;
      this.accessionNumber = accessionNumber;
      this.filingDate = filingDate;
      this.reportDate = reportDate;
      this.form = form;
      this.data = data;
    }

    public String getCik() {
      return cik;
    }
    public void setCik(String cik) {
      this.cik = cik;
    }

    public String getAccessionNumber() {
      return accessionNumber;
    }
    public void setAccessionNumber(String accessionNumber) {
      this.accessionNumber = accessionNumber;
    }
    
    public String getFilingDate() {
      return filingDate;
    }
    public void setFilingDate(String filingDate) {
      this.filingDate = filingDate;
    }
    
    public String getReportDate() {
      return reportDate;
    }
    public void setReportDate(String reportDate) {
      this.reportDate = reportDate;
    }

    public String getForm() {
      return form;
    }
    public void setForm(String form) {
      this.form = form;
    }

    public Object getData() {
      return data;
    }
    public void setData(Object data) {
      this.data = data;
    }
}
