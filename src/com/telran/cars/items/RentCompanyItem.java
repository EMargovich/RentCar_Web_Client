package com.telran.cars.items;

import java.time.LocalDate;

import com.telran.cars.models.IRentCompany;


import view.InputOutput;
import view.Item;

public abstract class RentCompanyItem implements Item {
	protected InputOutput inOut;
	protected IRentCompany company;
	protected String fileName;

	protected LocalDate from;
	protected LocalDate to;
	protected String format = "yyyy-MM-dd";

	public RentCompanyItem(InputOutput inOut, IRentCompany company, String fileName) {

		this.inOut = inOut;
		this.company = company;
		this.fileName = fileName;
	}

	protected String getRegNumberNotExisted() {
		String regNumber = inOut.inputString("Enter registration number");
		if (regNumber == null)
			return null;
		if (company.getCar(regNumber) != null) {
			inOut.outputLine("Car already exists");
			return null;
		}

		return regNumber;
	}

	protected String getRegNumberExisted() {
		String regNumber = inOut.inputString("Enter registration number");
		if (regNumber == null)
			return null;
		if (company.getCar(regNumber) == null) {
			inOut.outputLine("Car not found");
			return null;
		}

		return regNumber;
	}

	protected Long getLicenseIdIfNotExists() {
		Long id = inOut.inputLong("Enter license id");
		if (id == null)
			return null;
		if (company.getDriver(id) != null) {
			inOut.outputLine("Driver already exists");
			return null;
		}
		return id;
	}

	protected Long getLicenseIdIfExists() {
		Long id = inOut.inputLong("Enter license id");
		if (id == null)
			return null;
		if (company.getDriver(id) == null) {
			inOut.outputLine("Driver not found");
			return null;
		}
		return id;
	}

	protected void fillFromToDates() {
		from = inOut.inputDate("Enter date from " , format);
		if (from == null)
			return;
		to = inOut.inputDate("Enter date to ", format);
		if (to == null)
			return;

		if (from.isAfter(to)) {
			inOut.outputLine("Date from can't be after date to");
			from = to = null;
			return;
		}
	}

}
