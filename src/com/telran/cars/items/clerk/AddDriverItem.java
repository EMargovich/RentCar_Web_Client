package com.telran.cars.items.clerk;

import java.time.LocalDate;

import com.telran.cars.dto.Driver;
import com.telran.cars.items.RentCompanyItem;
import com.telran.cars.models.IRentCompany;

import view.InputOutput;

public class AddDriverItem extends RentCompanyItem {
	private static final int MIN_YEAR = LocalDate.now().minusYears(120).getYear();
	private static final int MAX_YEAR = LocalDate.now().minusYears(18).getYear();

	public AddDriverItem(InputOutput inOut, IRentCompany company, String fileName) {
		super(inOut, company, fileName);

	}

	@Override
	public String displayedName() {

		return "Add driver";
	}

	@Override
	public void perform() {
		Long licenseId = getLicenseIdIfNotExists();
		if (licenseId == null)
			return;
		String name = inOut.inputString("Enter driver name");
		if (name == null)
			return;

		Integer birthYear = inOut.inputInteger("Enter birth year from range", MIN_YEAR, MAX_YEAR);
		if (birthYear == null)
			return;

		String phone = inOut.inputString("Enter phone number");
		if (phone == null)
			return;
		Driver driver = new Driver(licenseId, name, birthYear, phone);
		inOut.outputLine(company.addDriver(driver));
		
	}

}
