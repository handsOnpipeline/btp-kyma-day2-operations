package dev.kyma.samples.easyfranchise.day2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.kyma.samples.easyfranchise.day2.jpa.entities.UserLoginInfo;
import dev.kyma.samples.easyfranchise.day2.jpa.repositories.UserLoginInfoRepository;
import dev.kyma.samples.easyfranchise.day2.rest.entities.UserMetric;

@Service
public class UserLoginInfoService {

	@Autowired
	private UserLoginInfoRepository userLoginInfoRepository;

	public List<UserMetric> userMetric(int month, int year) {
		return userLoginInfoRepository.getUserMetric(month, year);

	}

	public UserLoginInfo mergeUserLogin(UserLoginInfo info) {
		UserLoginInfo existingInfo = userLoginInfoRepository.findByTenantidAndUserAndYearAndMonth(info.getTenantid(),
				info.getUser(), info.getYear(), info.getMonth());

		if (existingInfo != null)
			return existingInfo;

		return userLoginInfoRepository.saveAndFlush(info);

	}
}
