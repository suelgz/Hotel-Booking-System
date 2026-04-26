import java.util.ArrayList;
import java.time.LocalDate;

public class Booking {


    private ArrayList<Room> rooms;
    private ArrayList<Customer> customers;
    private ArrayList<Reservation> reservations;


    public Booking() {
        rooms = new ArrayList<>();
        customers = new ArrayList<>();
        reservations = new ArrayList<>();
        initializeRooms();
    }

// initializes the system with default rooms
    private void initializeRooms() {
        addRoom(new StandardRoom("101", 2, 100.0, true));
        addRoom(new StandardRoom("102", 2, 100.0, true));
        addRoom(new StandardRoom("103", 4, 120.0, true));

        addRoom(new SuiteRoom("201", 2, 250.0, true, 50, "Deluxe", true));
        addRoom(new SuiteRoom("202", 6, 300.0, true, 75, "Presidential", true));
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }
    void addCustomer(Customer customer){
        customers.add(customer);
    }
    public void createReservation(String reservationId, Customer customer, Room room)
            throws RoomNotAvailableException { // if the room is not available

        if (!room.isAvailable()) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not available.");
        }

        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(3); // defauld 3 night stay
        Reservation reservation =
                new Reservation(reservationId, customer, room, checkIn, checkOut);

        reservations.add(reservation);
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }
    
 // returns only available rooms
    public ArrayList<Room> getAvailableRooms() {
        ArrayList<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

}



