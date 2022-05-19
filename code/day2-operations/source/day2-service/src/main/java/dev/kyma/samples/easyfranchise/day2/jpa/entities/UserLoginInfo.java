package dev.kyma.samples.easyfranchise.day2.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserLoginInfo {

	@Id
	@GeneratedValue
	private Long id;
	private String tenantid;
	private String user;
	private int year;
	private int month;

	public Long getId() {
		return id;
	}

	public String getTenantid() {
		return tenantid;
	}

	public String getUser() {
		return user;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public void setTenantid(String tenantid) {
		this.tenantid = tenantid;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setMonth(int month) {
		this.month = month;
	}

}
