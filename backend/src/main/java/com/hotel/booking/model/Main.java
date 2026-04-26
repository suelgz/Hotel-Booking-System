// THIS MAIN CLASS IS FOR TESTING ONLY

public class Main {

    public static void main(String[] args) {

        try {

            Booking booking = new Booking();
            System.out.println("Booking system started.\n");

           // customers
            Customer c1 = new Customer("Alex", "Williams", "alex@gmail.com", "4443332221");
            Customer c2 = new Customer("Barbara", "Scott", "barb@gmail.com", "1111222233");

            // adding customers to system
            booking.addCustomer(c1);
            booking.addCustomer(c2);


            System.out.println("Customers added:");
            System.out.println("- " + c1.getName());
            System.out.println("- " + c2.getName());
            System.out.println();

            //  available rooms
            System.out.println("Available rooms:");
            for (Room room : booking.getAvailableRooms()) {
                System.out.println(
                        room.getRoomNumber() +
                                " | Capacity: " + room.getCapacity() +
                                " | Price: " + room.getPrice()
                );

            }
            System.out.println();

            // selecting
            Room selectedRoom = booking.getAvailableRooms().get(0);
            Room selectedRoom2 = booking.getAvailableRooms().get(1);

            // creating reservation
            booking.createReservation("R001", c1, selectedRoom);
            booking.createReservation("R002", c2, selectedRoom2);

            Reservation reservation = booking.getReservations().get(0);
            Reservation reservation2 = booking.getReservations().get(1);

            // booking
            reservation.book();
            reservation2.book();

            System.out.println();

            //  cash payment
            Payment cashPayment = new CashPayment(
                    reservation.getTotalPrice(),
                    "P001",
                    "18-12-2025",
                    reservation.getTotalPrice()
            );

            reservation.makePayment(cashPayment);

            // credit card payment - customer 2
            Payment creditCardPayment = new CreditCardPayment(
                    reservation2.getTotalPrice(),   // amount
                    "P002",                          // payment id
                    "23-12-2025",                    // payment date
                    "1234-5678-9012-3456",           // card number
                    "Barbara Scott",              // card holder name
                    "12/27"                          // expiry
            );

            reservation2.makePayment(creditCardPayment);
            System.out.println();

          // trying to create a customer , shows exception handling
            try {
                Customer c3 = new Customer("Mark","Brown","mark@gmail.com", "");
                booking.addCustomer(c3);
                System.out.println("- " + c3.getName());
            } catch (InvalidCustomerDataException e) {
                System.out.println("Mark Brown couldn't be added: " + e.getMessage());
            }
            System.out.println("\nSystem finished.");

        } catch (InvalidCustomerDataException e) {
            System.out.println("Customer data error: " + e.getMessage());

    } catch (RoomNotAvailableException e) {
        System.out.println("Room error: " + e.getMessage());
    }
    }
}
