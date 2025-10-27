package com.telran.cars.items.manager;

import com.telran.cars.dto.*;
import com.telran.cars.items.RentCompanyItem;
import com.telran.cars.models.IRentCompany;

import view.InputOutput;

public class AddModelItem extends RentCompanyItem {
	private static final int MAX_TANK = 150;
	private static final int MIN_TANK =30;
	private static final int MAX_PRICE = 1500;
	private static final int MIN_PRICE =5;

	public AddModelItem(InputOutput inOut, IRentCompany company, String fileName) {
		super(inOut, company, fileName);
		
	}

	@Override
	public String displayedName() {
		
		return "add car model";
	}

	@Override
	public void perform() {
		String modelName = inOut.inputString("Enter model name");
		if(modelName==null) return;
		Integer gasTank = inOut.inputInteger("Enter gas tank volume in range", MIN_TANK, MAX_TANK);
		if(gasTank==null) return;
		String companyName = inOut.inputString("Enter company name");
		if(companyName==null) return;
		String country= inOut.inputString("Enter country name");
		if(country==null) return;
		Integer pricePerDay  = inOut.inputInteger("Enter price per day in range", MIN_PRICE, MAX_PRICE);;
		if(pricePerDay==null) return;
		Model model = new Model(modelName, gasTank, companyName, country, pricePerDay);
		inOut.outputLine(company.addModel(model));
		
	}

}
