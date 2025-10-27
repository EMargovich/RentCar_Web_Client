package com.telran;



import com.telran.cars.items.clerk.*;

import com.telran.cars.items.driver.*;

import com.telran.cars.items.manager.*;

import com.telran.cars.items.statist.*;

import com.telran.cars.items.technician.GetRecordItem;
import com.telran.cars.models.IRentCompany;


import com.telran.cars.models.RentCompanyTCPProxy;
import view.ConsoleInputOutput;
import view.ExitItem;
import view.InputOutput;
import view.Item;
import view.Menu;
import view.SubmenuItem;

public class RentCompanyApplItems {
	static IRentCompany company;
	static InputOutput inOut;
	static final String LOCAL_HOST ="localhost";
	static final int PORT = 30000;

	public static void main(String[] args) throws Exception {
		inOut = new ConsoleInputOutput();
		String hostname= LOCAL_HOST;
		int port = PORT;
		company = new RentCompanyTCPProxy(hostname, port);
		Menu menu =new Menu(getMainMenuItems(), inOut);
		menu.runMenu();
	}

	private static Item[] getMainMenuItems() {
		Item[] items = {
		new SubmenuItem("Clerk", inOut, getClerkItems()),
		new SubmenuItem("Driver", inOut, getDriverItems()),
		new SubmenuItem("Manager", inOut, getManagerItems()),
		new SubmenuItem("Statist", inOut, getStatistItems()),
		new SubmenuItem("Technician", inOut, getTechnicianItems()),
		new ExitItem()
		};
		return items;
	}

	private static Item[] getTechnicianItems() {
		Item[]items = {
				new GetRecordItem(inOut, company, LOCAL_HOST),
				new ExitItem()
		};
		
		return items;
	}

	private static Item[] getStatistItems() {
		Item[]items = {
				new GetMostActiveDriversItem(inOut, company, LOCAL_HOST),
				new GetMostPopularModelsItem(inOut, company, LOCAL_HOST),
				new GetMostProfitableItem(inOut, company, LOCAL_HOST),
				new ExitItem()
		};
		
		return items;
	}

	private static Item[] getManagerItems() {
		Item[]items = {
				new AddModelItem(inOut, company, LOCAL_HOST),
				new AddCarItem(inOut, company, LOCAL_HOST),
				new RemoveCarItem(inOut, company, LOCAL_HOST),
				new RemoveModelItem(inOut, company, LOCAL_HOST),
				new ExitItem()
		};
		
		return items;
	}

	private static Item[] getDriverItems() {
		Item[]items = {
				new GetCarItem(inOut, company, LOCAL_HOST),
				new GetDriverItem(inOut, company, LOCAL_HOST),
				new GetCarsByDriverItem(inOut, company, LOCAL_HOST),
				new GerDriversByCarItem(inOut, company, LOCAL_HOST),
				new ExitItem()
		};
		
		return items;
	}

	private static Item[] getClerkItems() {
		Item[]items = {
				new AddDriverItem(inOut, company, LOCAL_HOST),
				new GetCarByModelItem(inOut, company, LOCAL_HOST),
				new RentCarItem(inOut, company, LOCAL_HOST),
				new ReturnCarItem(inOut, company, LOCAL_HOST),
				new ExitItem()
		};
		
		return items;
	}

}
