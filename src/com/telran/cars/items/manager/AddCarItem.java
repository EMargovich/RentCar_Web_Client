package com.telran.cars.items.manager;

import java.util.List;

import com.telran.cars.dto.*;
import com.telran.cars.items.RentCompanyItem;
import com.telran.cars.models.IRentCompany;

import view.InputOutput;

public class AddCarItem extends RentCompanyItem {

	public AddCarItem(InputOutput inOut, IRentCompany company, String fileName) {
		super(inOut, company, fileName);
		
	}

	@Override
	public String displayedName() {
		
		return "Add new car";
	}

	@Override
	public void perform() {
		String regNumber = getRegNumberNotExisted();
		if(regNumber==null) return;
		String color =inOut.inputString("Enter color");
		if(color==null)return;
		List<String> models= company.getModelNames();
		String modelName= inOut.inputString("Enter model name from list",models);
		Car car = new Car(regNumber, color, modelName);
		
		inOut.outputLine(company.addCar(car));
		
	}

}
