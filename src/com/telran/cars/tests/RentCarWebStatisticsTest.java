package com.telran.cars.tests;

import com.telran.cars.dto.Car;
import com.telran.cars.dto.Driver;
import com.telran.cars.dto.Model;
import com.telran.cars.models.IRentCompany;
import com.telran.cars.models.RentCompanyTCPProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//If this test do not work you need to check fine percent and gas rate in abstract class.
//They must be 8 and 13. We can not set them there because theirs setters do not realised in client-server version.
public class RentCarWebStatisticsTest {
    private static final String MODEL = "model1";
    private static final String CAR = "car";
    private IRentCompany company;
    int[] prices = {100, 100, 100, 100, 1000};
    int[] years = {2000, 1995, 1950, 1948};
    LocalDate fromDate = LocalDate.of(1900, 1, 1);
    LocalDate toDate = LocalDate.of(2100, 1, 1);
    LocalDate rentDate = LocalDate.of(2020, 1, 1);

    static final String LOCAL_HOST ="localhost";
    static final int PORT = 30000;

    @BeforeEach
    void setUp() throws Exception {

//        String hostname= LOCAL_HOST;
//        int port = PORT;
//        company = new RentCompanyTCPProxy(hostname, port);33
        company = new RentCompanyTCPProxy(LOCAL_HOST, PORT);
//      Theirs parameters I set in abstract class in API. We did not make this methods in server-client version.
//        company.setFinePercent(8);
//        company.setGasPrice(13);
        company.clear_company();

        createModels();
        createCars();
        createDrivers();
        rentReturns();

    }

    private void rentReturns() {
        int[] licenceId = {0,0,1,1,2,2,3,3,0};
        String [] regNumber = {CAR + 0, CAR + 1, CAR + 0, CAR + 1, CAR + 2
                , CAR +3, CAR + 2, CAR + 3, CAR + 4};
        int rentDays = 5;
        LocalDate cursor = rentDate;

        for (int i = 0; i < regNumber.length; i++) {
            company.rentCar(regNumber[i], licenceId[i], cursor, rentDays);
            company.returnCar(regNumber[i], licenceId[i]
                    , cursor.plusDays(rentDays), 0, 100 );
            cursor = cursor.plusDays(rentDays + 1);
        }
    }

    private void createDrivers() {
        for (int licenseId = 0; licenseId < years.length; licenseId++) {
            company.addDriver(new Driver(licenseId, "name" + licenseId
                    , years[licenseId], "phone" + licenseId));
        }
    }

    private void createCars() {
        for (int i = 0; i < prices.length; i++) {
            company.addCar(new Car(CAR + i
                    , "color" + 1, MODEL + i));
        }
    }

    private void createModels() {
        for (int i = 0; i < prices.length; i++) {
            company.addModel(new Model(MODEL + i
                    , 50, "company" + i
                    , "couinty" + i, prices[i]));
        }
    }

    @Test
    void testGetMostPopularCarModels() {
        int ageYoungFrom = rentDate.getYear() - years[0];
        int ageYoungTo = rentDate.getYear() - years[1] + 1;
        int ageOldFrom = rentDate.getYear() - years[2];
        int ageOldTo = rentDate.getYear() - years[3] + 1;

        List<String> youngModels = company
                .getMostPopularCarModels(fromDate, toDate
                        , ageYoungFrom, ageYoungTo);
        assertEquals(2, youngModels.size());
        assertTrue(youngModels.contains(MODEL+1));
        assertTrue(youngModels.contains(MODEL+0));

        List<String> oldModels = company
                .getMostPopularCarModels(fromDate, toDate
                        , ageOldFrom, ageOldTo);
        assertEquals(2, oldModels.size());
        assertTrue(oldModels.contains(MODEL+2));
        assertTrue(oldModels.contains(MODEL+3));

    }

    @Test
    void testGetMostProfitableCarModels() {
        List<String> profitable = company.getMostProfitableCarModels(fromDate, toDate);
        assertEquals(1, profitable.size());
        assertEquals(MODEL+4, profitable.get(0));
    }

    @Test
    void testGetMostActiveDrivers() {
        List<Driver> activeDrivers = company.getMostActiveDrivers();
        assertEquals(1, activeDrivers.size());
        assertTrue(activeDrivers.contains(company.getDriver(0)));
        assertEquals(0L, activeDrivers.get(0).getLicenseId());
    }

    @Test
    void testGetMostPopularCarModels_Negative() {
        int ageYoungFrom = rentDate.getYear() - years[0];
        int ageYoungTo = rentDate.getYear() - years[1] + 1;
        int ageOldFrom = rentDate.getYear() - years[2];
        int ageOldTo = rentDate.getYear() - years[3] + 1;
        List<String> negative1 = company
                .getMostPopularCarModels(fromDate, fromDate.plusYears(50)
                        , ageYoungFrom, ageYoungTo);

        assertTrue(negative1.isEmpty());
        List<String> negative2 = company
                .getMostPopularCarModels(fromDate, fromDate.plusYears(50)
                        , 40, 50);
        assertTrue(negative2.isEmpty());
    }

    @Test
    void testGetMostActiveDrivers1() {
        company.removeCar(CAR+4);

        List<Driver> activeDrivers = company.getMostActiveDrivers();
        assertEquals(4, activeDrivers.size());
        assertTrue(activeDrivers.contains(company.getDriver(0)));
        assertTrue(activeDrivers.contains(company.getDriver(1)));
        assertTrue(activeDrivers.contains(company.getDriver(2)));
        assertTrue(activeDrivers.contains(company.getDriver(3)));

        //assertEquals(0L, activeDrivers.get(0).getLicenseId());

        List<String> youngModels = company
                .getMostPopularCarModels(fromDate, toDate
                        , 0, 120);
        assertEquals(4, youngModels.size());
        assertTrue(youngModels.contains(MODEL+1));
        assertTrue(youngModels.contains(MODEL+0));
        assertTrue(youngModels.contains(MODEL+2));
        assertTrue(youngModels.contains(MODEL+3));
    }
}
