package org.foxconn.entity;



public class PcsResult {
	String writeline;
	String ssn;
	String shiporderno;
	String custpartno;
	
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getShiporderno() {
		return shiporderno;
	}
	public void setShiporderno(String shiporderno) {
		this.shiporderno = shiporderno;
	}
	public String getCustpartno() {
		return custpartno;
	}
	public void setCustpartno(String custpartno) {
		this.custpartno = custpartno;
	}
	public String getWriteline() {
		return writeline;
	}
	@Override
	public String toString() {
		return "PcsResult [writeline=" + writeline + ", ssn=" + ssn + ", shiporderno=" + shiporderno
				+ ", custpartno=" + custpartno + ", getSsn()=" + getSsn() + ", getShiporderno()=" + getShiporderno()
				+ ", getCustpartno()=" + getCustpartno() + ", getWriteline()=" + getWriteline() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	public void setWriteline(String writeline) {
		this.writeline = writeline;
	}
	
	
}
