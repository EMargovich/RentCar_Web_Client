package com.telran.cars.items.driver;

import com.telran.cars.items.RentCompanyItem;
import com.telran.cars.models.IRentCompany;

import view.InputOutput;

public class GetCarItem extends RentCompanyItem {

	public GetCarItem(InputOutput inOut, IRentCompany company, String fileName) {
		super(inOut, company, fileName);
		
	}

	@Override
	public String displayedName() {
	
		return "Display car's data";
	}

	@Override
	public void perform() {
		String regNumber = getRegNumberExisted();
		if(regNumber==null) return;
		inOut.outputLine(company.getCar(regNumber));
	}

}
