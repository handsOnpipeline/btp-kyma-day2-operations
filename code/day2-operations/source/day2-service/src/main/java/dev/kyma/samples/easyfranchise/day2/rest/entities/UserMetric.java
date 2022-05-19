package dev.kyma.samples.easyfranchise.day2.rest.entities;

public class UserMetric {

	private String tenantid;
	private long activeUsers;

	public UserMetric(String tenatid, long activeUsers) {
		super();
		this.tenantid = tenatid;
		this.activeUsers = activeUsers;
	}

	public String getTenantid() {
		return tenantid;
	}

	public long getActiveUsers() {
		return activeUsers;
	}

	@Override
	public String toString() {
		return "UserMetric [tenantid=" + tenantid + ", activeUsers=" + activeUsers + "]";
	}

}
