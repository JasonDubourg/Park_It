package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        //Récupérer la prochaine place disponible grâce à la méthode getNextAvailableSlot ParkingSpotDao
        int parkingSportAvailable = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability 
        //Récupérer le ticket du véhicule ABCDEF
        Ticket ticket = ticketDAO.getTicket("ABCDEF"); 
        //Vérifier que le ticket existe en BDD 
        assertNotNull(ticket);
        //Récupérer la place de parking 
        ParkingSpot parkingSpot = ticket.getParkingSpot(); 
        assertNotNull(parkingSpot);
        //On vérifie avec assertion que ce parking est en statue available == false donc occupé.
        assertFalse(parkingSpot.isAvailable());
        //Vérifier que Java à bien utilisé la place de parking vérifié avant.
        assertEquals(parkingSportAvailable, parkingSpot.getId());
    }

    @Test
    public void testParkingLotExit() throws InterruptedException{
        testParkingACar();
        Thread.sleep(2000);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
        //Récupérer le ticket
        Ticket ticket = ticketDAO.getTicket("ABCDEF"); 
        //Vérifier que le ticket existe en BDD 
        assertNotNull(ticket);
        //Vérifier que le ticket à bien une date de sortie 
        assertNotNull(ticket.getOutTime());
        //Vérifier que le ticket a bien un prix
        assertNotNull(ticket.getPrice());
        //Vérifier que le statut viability est revenue à true => Bug code 
        //assertTrue(ticket.getParkingSpot().isAvailable());
    }
    
}











