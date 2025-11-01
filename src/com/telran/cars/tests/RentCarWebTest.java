package com.telran.cars.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.telran.cars.models.RentCompanyTCPProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.telran.cars.dto.*;
import com.telran.cars.dto.enums.State;
import com.telran.cars.models.IRentCompany;

import static com.telran.cars.dto.enums.CarsReturnCode.*;

class RentCarWebTest {
    final String MODEL_NAME = "Model1";
    final int GAS_TANK = 50;
    final int PRICE_PER_DAY = 200;
    final long LICENSE = 1000L;
    final String REG_NUMBER = "100";
    final String COMPANY = "Company1";
    final String COUNTRY = "Country1";
    final String COLOR = "color1";
    final String NAME = "name1";
    final int YEAR_OB = 1990;
    final String PHONE = "123456789";
    final LocalDate RENT_DATE = LocalDate.of(2025, 8, 1);
    final int RENT_DAYS = 3;

    // ==================
    final int GAS_PRICE = 10;
    final int FINE_PERCENT = 15;

    private Model model;
    private Car car;
    private Driver driver;

    private IRentCompany company;
    static final String LOCAL_HOST ="localhost";
    static final int PORT = 30000;


    @BeforeEach
    void setUp() throws Exception {
        model = new Model(MODEL_NAME, GAS_TANK, COMPANY, COUNTRY, PRICE_PER_DAY);
        car = new Car(REG_NUMBER, COLOR, MODEL_NAME);
        driver = new Driver(LICENSE, NAME, YEAR_OB, PHONE);
        company = new RentCompanyTCPProxy(LOCAL_HOST, PORT);
       // ((Persistable) company).save("companyWeb.data");
        company.clear_company();
    }

    @Test
    void testAdd_get_Entities_OK() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(model, company.getModel(MODEL_NAME));
        assertEquals(car, company.getCar(REG_NUMBER));
        assertEquals(driver, company.getDriver(LICENSE));
    }

    @Test
    void testAdd_car_NO_MODEL() {
        assertEquals(NO_MODEL, company.addCar(car));
        assertNull(company.getCar(REG_NUMBER));
    }

    @Test
    void testAdd_Duplicate_entities() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(MODEL_EXISTS, company.addModel(model));
        assertEquals(CAR_EXISTS, company.addCar(car));
        assertEquals(DRIVER_EXISTS, company.addDriver(driver));
    }

    @Test
    void test_get_when_not_added() {
        assertNull(company.getCar(REG_NUMBER));
        assertNull(company.getDriver(LICENSE));
        assertNull(company.getModel(MODEL_NAME));
    }

//    @Test
//    void testSave_Restored_OK() {
//        assertEquals(OK, company.addModel(model));
//        assertEquals(OK, company.addCar(car));
//        assertEquals(OK, company.addDriver(driver));
//
//        String file = "companyTest.data";
//        ((Persistable) company).save(file);
//        IRentCompany restored = RentCompanyEmbedded.restoreFromFile(file);
//        assertNotNull(restored);
//        assertEquals(model, restored.getModel(MODEL_NAME));
//        assertEquals(car, restored.getCar(REG_NUMBER));
//        assertEquals(driver, restored.getDriver(LICENSE));
//
//    }
//
//    @Test
//    void testSave_Restored_Fail() {
//        String file = "companyTest_fail.data";
//        IRentCompany restored = RentCompanyEmbedded.restoreFromFile(file);
//        assertNotNull(restored);
//        assertTrue(restored instanceof RentCompanyEmbedded);
//        assertNull(restored.getModel(MODEL_NAME));
//        assertNull(restored.getCar(REG_NUMBER));
//        assertNull(restored.getDriver(LICENSE));
//    }

    // sprint 2
    @Test
    void testRentCarOK() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
    }

    @Test
    void testRentCarNO_CAR() {
        assertEquals(OK, company.addModel(model));
        // assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(NO_CAR, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
    }

    @Test
    void testRentCarNO_Driver() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
//		assertEquals(OK, company.addDriver(driver));

        assertEquals(NO_DRIVER, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
    }

    //We can't use this test in server-client version because in its we use setters for car, which set parameters for car only on client.
    // This setter do not realise in client-server API. When we use this setter we don't change parameter on server.
//    @Test
//    void testRentCarRemove_Use() {
//        assertEquals(OK, company.addModel(model));
//        assertEquals(OK, company.addCar(car));
//        assertEquals(OK, company.addDriver(driver));
//        car.setFlRemoved(true);
//        assertEquals(CAR_REMOVED, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
//        car.setFlRemoved(false);
//        car.setInUse(true);
//        assertEquals(CAR_IN_USE, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
//    }

    @Test
    void testRentCarRemove_Use() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS);
        assertEquals(CAR_IN_USE, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
        company.removeCar(REG_NUMBER);
        assertEquals(CAR_REMOVED, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
    }

    @Test
    void testRentRecords() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));
        RentRecord expected = new RentRecord(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS);
        company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS);
        List<RentRecord> res = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(RENT_DAYS));
        assertEquals(1, res.size());
        assertEquals(expected, res.get(0));
        List<RentRecord> res1 = company.getRentRecordsAtDate(RENT_DATE.plusDays(RENT_DAYS), RENT_DATE.plusDays(10));
        assertTrue(res1.isEmpty());
    }

    @Test
    void testCarsByDriver() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));
        company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS);
        List<Car> res = company.getCarsByDriver(LICENSE);
        assertEquals(1, res.size());
        assertEquals(car, res.get(0));

        res = company.getCarsByDriver(LICENSE + 1);
        assertTrue(res.isEmpty());

    }

    @Test
    void testDriversByCar() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));
        company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS);
        List<Driver> res = company.getDriversByCar(REG_NUMBER);
        assertEquals(1, res.size());
        assertEquals(driver, res.get(0));

        res = company.getDriversByCar(REG_NUMBER + 1);
        assertTrue(res.isEmpty());

    }

    @Test
    void testCarsByModel() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));
        List<Car> res = company.getCarsByModel(MODEL_NAME);
        assertEquals(1, res.size());
        assertEquals(car, res.get(0));

        Car car1 = new Car(REG_NUMBER + 1, COLOR + "R", MODEL_NAME);

        assertEquals(OK, company.addCar(car1));
        //car1.setInUse(true);
        company.rentCar(REG_NUMBER+1, LICENSE, RENT_DATE, RENT_DAYS);
        res = company.getCarsByModel(MODEL_NAME);
        assertEquals(1, res.size());
        assertEquals(car, res.get(0));

//        car1.setInUse(false);
//        car1.setFlRemoved(true);
//
//        res = company.getCarsByModel(MODEL_NAME);
//        assertEquals(1, res.size());
//        assertEquals(car, res.get(0));
//        res = company.getCarsByModel(MODEL_NAME + 11);
//        assertTrue(res.isEmpty());
    }

    @Test
    void testRemoveCarsInUse() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));
        company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS);
        RemovedCarData nC = new RemovedCarData(car, null);
        assertEquals(nC, company.removeCar(REG_NUMBER));
        assertNull(company.removeCar(REG_NUMBER + 100));
        car.setFlRemoved(true);
        assertNull(company.removeCar(REG_NUMBER));

    }

    @Test
    void testRemoveCarsNotInUse() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));
        RemovedCarData nC = new RemovedCarData(car, new ArrayList<>());
        assertEquals(nC, company.removeCar(REG_NUMBER));

    }

    // ========== RETURN CAR: стоимость и состояние (без getRecords) ==============

    @Test
    void returnCar_noDelay_fullTank_onlyBase_andStateExcellent() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));

        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(RENT_DAYS), 0, 100);
        assertNotNull(rcd);
        assertNotNull(rcd.getCar());
        assertFalse(rcd.getCar().isInUse());
        assertEquals(State.EXCELLENT, rcd.getCar().getState());

        double expected = PRICE_PER_DAY * RENT_DAYS;
        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertEquals(1, recs.size());
        assertEquals(expected, recs.get(0).getCost(), 0.001);
    }

    @Test
    void returnCar_withDelay_addsFine() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));

        // задержка на 2 дня
        LocalDate returnDate = RENT_DATE.plusDays(RENT_DAYS + 2);
        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE, returnDate, 0, 100);
        assertNotNull(rcd);
        assertFalse(rcd.getCar().isInUse());

        double base = PRICE_PER_DAY * RENT_DAYS;
        double finePerDay = PRICE_PER_DAY * (1 + FINE_PERCENT / 100.0);
        double expected = base + 2 * finePerDay;

        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertEquals(1, recs.size());
        assertEquals(expected, recs.get(0).getCost(), 0.001);
    }

    @Test
    void returnCar_noFullTank_addsFuel_andStateByDamages() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertEquals(0, recs.size());
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));

        // бак 50%, повреждения 10 → GOOD (GOOD_THRESHOLD = 10)
        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(RENT_DAYS), 10, 50);
        assertNotNull(rcd);
        assertEquals(State.GOOD, rcd.getCar().getState());

        double base = PRICE_PER_DAY * RENT_DAYS;
        double fuel = GAS_TANK * ((100 - 50) / 100.0) * GAS_PRICE; // 50 * 0.5 * 13 = 325
        double expected = base + fuel;

        recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertEquals(1, recs.size());
        assertEquals(expected, recs.get(0).getCost(), 0.001);
    }

    @Test
    void returnCar_heavyDamages_removesCarAndCleansIndexes() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));

        // повреждения > 60 → фактическое удаление
        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE,
                RENT_DATE.plusDays(RENT_DAYS), 70, 100);
        assertNotNull(rcd);
        assertNull(company.getCar(REG_NUMBER)); // из каталога удалена

        // записи по датам очищены
        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertTrue(recs.isEmpty());
    }

    // ========== REMOVE CAR (без getRecords) ============

    @Test
    void removeCar_whenNotInUse_removesFully_andRecordsGoneFromIndexes() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
        // возвращаем, чтобы машина была свободна и записи появились
        company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(RENT_DAYS), 0, 100);

        RemovedCarData rcd = company.removeCar(REG_NUMBER);
        assertNotNull(rcd);
        assertNotNull(rcd.getCar());
        assertNull(company.getCar(REG_NUMBER)); // машина исчезла из картотеки

        // и записи по датам тоже очищены
        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertTrue(recs.isEmpty());
    }

    @Test
    void removeCar_whenInUse_onlyFlagsRemoved_untilReturn() {
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));

        RemovedCarData rcd = company.removeCar(REG_NUMBER);
        assertNotNull(rcd);
        assertNotNull(rcd.getCar());
        // машина остаётся в каталоге, но помечена удалённой и всё ещё InUse
        Car stillThere = company.getCar(REG_NUMBER);
        assertNotNull(stillThere);
        assertTrue(stillThere.isFlRemoved());
        assertTrue(stillThere.isInUse());

        // теперь возвращаем — должно удалиться физически
        company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(RENT_DAYS), 0, 100);
        assertNull(company.getCar(REG_NUMBER));

        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertTrue(recs.isEmpty());
    }

    // ========= возврат машины (расширение покрытия) ========

    @Test
    void returnCar_delayAndFuel_addsBothPartsToCost() {
        // задержка 1 день + недолив 40% → база + штраф + топливо
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));

        LocalDate returnDate = RENT_DATE.plusDays(RENT_DAYS + 1);
        int damages = 0;
        int tankPercent = 60;

        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE,
                returnDate, damages, tankPercent);
        assertNotNull(rcd);
        assertNotNull(rcd.getCar());
        assertFalse(rcd.getCar().isInUse());
        assertEquals(State.EXCELLENT, rcd.getCar().getState());

        double base = PRICE_PER_DAY * RENT_DAYS;
        double finePerDay = PRICE_PER_DAY * (1 + FINE_PERCENT / 100.0);
        double fuelLiters = GAS_TANK * (1 - tankPercent / 100.0);
        double fuelCost = fuelLiters * GAS_PRICE;
        double expected = base + finePerDay + fuelCost;

        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertEquals(1, recs.size());
        assertEquals(expected, recs.get(0).getCost(), 0.001);
    }

    @Test
    void returnCar_zeroDelay_partialFuel_onlyFuelAdded() {
        // без просрочки, бак 80% → только топливо к базе
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS - 1));

        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE,
                RENT_DATE.plusDays(RENT_DAYS - 1), 0, 80);
        assertNotNull(rcd);
        assertFalse(rcd.getCar().isInUse());
        assertEquals(State.EXCELLENT, rcd.getCar().getState());

        double base = PRICE_PER_DAY * (RENT_DAYS - 1);
        double fuel = GAS_TANK * 0.20 * GAS_PRICE; //
        double expected = base + fuel;

        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertEquals(1, recs.size());
        assertEquals(expected, recs.get(0).getCost(), 0.001);
    }

    @Test
    void returnCar_state_damage1_isEXCELLENT_and_damage11_isGOOD() {
        // Проверяем обе стороны границы 10:
        // 1 -> EXCELLENT, 11 -> GOOD
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        // damage = 1 -> EXCELLENT
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, 1));
        RemovedCarData r1 = company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(1), 1, 100);
        assertNotNull(r1);
        assertEquals(State.EXCELLENT, r1.getCar().getState());
        assertFalse(r1.getCar().isInUse());

        // Снова сдаём в аренду и возвращаем с damage = 11 -> GOOD
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(5), 1));
        RemovedCarData r2 = company.returnCar(REG_NUMBER, LICENSE,
                RENT_DATE.plusDays(6), 11, 100);
        assertNotNull(r2);
        assertEquals(State.GOOD, r2.getCar().getState());
        assertFalse(r2.getCar().isInUse());
        assertNotNull(company.getCar(REG_NUMBER)); // GOOD — машина остаётся в каталоге
        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertEquals(2, recs.size());
    }

    @Test
    void returnCar_state_damage60_isBAD_notRemoved_and_damage61_removed() {
        // 60 → BAD и остаётся; 61 → удаляем и чистим индексы
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        // damage = 60
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, 1));
        RemovedCarData r1 = company.returnCar(REG_NUMBER, LICENSE,
                RENT_DATE.plusDays(1), 60, 100);
        assertEquals(State.BAD, r1.getCar().getState());
        assertNotNull(company.getCar(REG_NUMBER));

        // damage = 61
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(3), 1));
        RemovedCarData r2 = company.returnCar(REG_NUMBER, LICENSE,
                RENT_DATE.plusDays(4), 61, 100);
        assertNotNull(r2);
        assertNull(company.getCar(REG_NUMBER));

        List<RentRecord> after = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(30));
        assertTrue(after.isEmpty(), "History should be cleared when deleting a machine");
    }

    @Test
    void returnCar_wrongCar_keepsInUse_andNoRecordsWritten() {
        // Возврат “не той” машины — аренда не закрывается и записи не меняются
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, 2));

        String wrongReg = REG_NUMBER + "X";
        RemovedCarData rcd = company.returnCar(wrongReg, LICENSE, RENT_DATE.plusDays(2), 0, 100);
        assertNotNull(rcd);
        assertNull(rcd.getCar(), "Contract: if the car is invalid, return null in the car field");
        assertTrue(company.getCar(REG_NUMBER).isInUse(), "Rent is not closed");

        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(10));
        // Запись аренды должна остаться одной и без стоимости возврата
        assertEquals(1, recs.size());
        assertEquals(0.0, recs.get(0).getCost(), 0.001, "The cost should not be considered for incorrect return.");
    }

    @Test
    void returnCar_wrongDriver_keepsInUse_andNoRecordsWritten() {
        // Возврат не тем водителем — аналогично: аренда не закрывается
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, 2));

        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE + 1, RENT_DATE.plusDays(2), 0, 100);
        assertNotNull(rcd);
        assertNull(rcd.getCar());
        assertTrue(company.getCar(REG_NUMBER).isInUse());

        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(10));
        assertEquals(1, recs.size());
        assertEquals(0.0, recs.get(0).getCost(), 0.001);
    }

    @Test
    void returnCar_notRented_returnsNullCar_andNoChanges() {
        // Возврат неарендованной машины — ничего не меняется
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(1), 0, 100);
        assertNotNull(rcd);
        assertNull(rcd.getCar());
        assertFalse(company.getCar(REG_NUMBER).isInUse());

        List<RentRecord> recs = company.getRentRecordsAtDate(RENT_DATE, RENT_DATE.plusDays(10));
        assertTrue(recs.isEmpty());
    }

    @Test
    void returnCar_afterPositiveReturn_canRentAgain() {
        // После корректного возврата машина снова доступна к аренде
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, RENT_DAYS));
        RemovedCarData rcd = company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(RENT_DAYS), 0, 100);
        assertNotNull(rcd);
        assertFalse(company.getCar(REG_NUMBER).isInUse());

        // Повторная аренда
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(10), RENT_DAYS));
        assertTrue(company.getCar(REG_NUMBER).isInUse());
    }

    // ========== ДОБАВЛЕННЫЕ ТЕСТЫ: удаление машины (расширение покрытия)
    // ==============

    @Test
    void removeCar_idempotent_onAlreadyRemovedAndNotExists() {
        // Удаляем свободную машину → потом повторяем удаление и пробуем
        // «несуществующую»
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));

        // делаем историю: аренда → возврат
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, 1));
        company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(1), 0, 100);

        // первое удаление — успешно
        RemovedCarData first = company.removeCar(REG_NUMBER);
        assertNotNull(first);
        assertNull(company.getCar(REG_NUMBER));

        // повторное — null (идемпотентно)
        assertNull(company.removeCar(REG_NUMBER));

        // несуществующая
        assertNull(company.removeCar(REG_NUMBER + "_NO"));
    }

    @Test
    void removeCar_inUse_excludedFromModelList_untilReturn() {
        // Помеченная к удалению занятая машина не должна попадать в getCarsByModel
        assertEquals(OK, company.addModel(model));
        assertEquals(OK, company.addCar(car));
        assertEquals(OK, company.addDriver(driver));
        assertEquals(OK, company.rentCar(REG_NUMBER, LICENSE, RENT_DATE, 3));

        RemovedCarData rcd = company.removeCar(REG_NUMBER);
        assertNotNull(rcd);
        assertTrue(company.getCar(REG_NUMBER).isFlRemoved());
        assertTrue(company.getCar(REG_NUMBER).isInUse());

        List<Car> available = company.getCarsByModel(MODEL_NAME);
        assertTrue(available.isEmpty(), "A remote (and busy) car should not be shown by model");

        // после возврата — физически удаляется и из каталога, и из выборок
        company.returnCar(REG_NUMBER, LICENSE, RENT_DATE.plusDays(3), 0, 100);
        assertNull(company.getCar(REG_NUMBER));

        available = company.getCarsByModel(MODEL_NAME);
        assertTrue(available.isEmpty());
    }
}
