package com.telran.cars.items;

import com.telran.utils.Persistable;

import view.InputOutput;

public class SaveAndExitItem extends RentCompanyItem {

	public SaveAndExitItem(InputOutput inOut, IRentCompany company, String fileName) {
		super(inOut, company, fileName);
	}

	@Override
	public String displayedName() {

		return "Save and exit";
	}

	@Override
	public void perform() {
		((Persistable) company).save(fileName);

	}

	public boolean isExit() {
		return true;
	}
}
