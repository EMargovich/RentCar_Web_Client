package com.telran.cars.items.driver;



import com.telran.cars.dto.Driver;
import com.telran.cars.items.RentCompanyItem;
import com.telran.cars.models.IRentCompany;

import view.InputOutput;

public class GetDriverItem extends RentCompanyItem {

	public GetDriverItem(InputOutput inOut, IRentCompany company, String fileName) {
		super(inOut, company, fileName);
	
	}

	@Override
	public String displayedName() {
		
		return "Display driver's name";
	}

	@Override
	public void perform() {
		Long licenseId = getLicenseIdIfExists();
		if (licenseId == null)
			return;
		Driver driver =  company.getDriver(licenseId);
		inOut.outputLine(driver);
	}

}
