package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithLessThanThirtyMinParkingTime() {
    	 Date inTime = new Date();
         inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000 - 1000)); // 29min59sec parking time
         Date outTime = new Date();
         ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
         ticket.setInTime(inTime);
         ticket.setOutTime(outTime);
         ticket.setParkingSpot(parkingSpot);
         fareCalculatorService.calculateFare(ticket);
         assertEquals(0 , ticket.getPrice());
    }
    
    @Test 
    public void calculateFareBikeWithLessThanThirtyMinParkingTime() {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000 - 1000)); // 29min59sec parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0 , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithMoreThanThirtyMinAndLessThanOneHourParkingTime() {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000)); // 30min parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false); 
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.5 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareBikeWithMoreThanThirtyMinAndLessThanOneHourParkingTime() {
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000)); // 30min parking time
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.5 * Fare.BIKE_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareCarWithFivePourcentOffRegularCustomer() {
    	Date inTime1 = new Date();
        Date outTime1 = new Date();
        inTime1.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000)); // 45 min parking time
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false); 
        ticket.setInTime(inTime1);
        ticket.setOutTime(outTime1);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, Fare.DISCOUNT_REGULAR_CUSTOMER);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR * (1 - Fare.DISCOUNT_REGULAR_CUSTOMER / 100d)) , ticket.getPrice());
    }
    
    @Test
    public void calculateFareBikeWithFivePourcentOffRegularCustomer() {
    	Date inTime1 = new Date();
        Date outTime1 = new Date();
        inTime1.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000)); // 45 min parking time
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false); 
        ticket.setInTime(inTime1);
        ticket.setOutTime(outTime1);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, Fare.DISCOUNT_REGULAR_CUSTOMER);
        assertEquals((24 * Fare.BIKE_RATE_PER_HOUR * (1 - Fare.DISCOUNT_REGULAR_CUSTOMER / 100d)) , ticket.getPrice());
    }
}
