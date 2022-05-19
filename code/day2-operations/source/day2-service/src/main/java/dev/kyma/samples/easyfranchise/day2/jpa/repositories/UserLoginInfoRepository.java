package dev.kyma.samples.easyfranchise.day2.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.kyma.samples.easyfranchise.day2.jpa.entities.UserLoginInfo;
import dev.kyma.samples.easyfranchise.day2.rest.entities.UserMetric;

public interface UserLoginInfoRepository extends JpaRepository<UserLoginInfo, Long> {

	@Query("SELECT new dev.kyma.samples.easyfranchise.day2.rest.entities.UserMetric(tenantid, COUNT(user)) " + //
			"FROM  UserLoginInfo WHERE MONTH=?1 AND YEAR=?2 " + //
			"GROUP BY tenantid")
	public List<UserMetric> getUserMetric(int month, int year);

	public UserLoginInfo findByTenantidAndUserAndYearAndMonth(String tenantid, String user, int year, int month);

}
