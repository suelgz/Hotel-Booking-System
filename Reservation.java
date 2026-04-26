import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation implements Bookable, Cancelable {

    private String reservationId;
    private Customer customer;
    private Room room;
    private double totalPrice;
    private boolean isBooked;
    private boolean isCancelled;
    private Payment payment;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;


    public Reservation(String reservationId, Customer customer, Room room, LocalDate checkInDate,
                       LocalDate checkOutDate) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.isBooked = false;
        this.isCancelled = false;
    }


    public String getReservationId() {
        return reservationId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Room getRoom() {
        return room;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Payment getPayment() {
        return payment;
    }
    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }



    @Override
    public void book() {
        if (room.isAvailable() && !isBooked && !isCancelled) {
            this.isBooked = true;
            room.setAvailable(false);
            int nights = (int) getNumberOfNights();
            this.totalPrice = room.calculatePrice(nights);

            System.out.println("Reservation " + reservationId + " booked!");
            System.out.println("Customer: " + customer.getName());
            System.out.println("Room: " + room.getRoomNumber());
            System.out.println("Check-in: " + checkInDate);
            System.out.println("Check-out: " + checkOutDate);
            System.out.println("Nights: " + nights);
            System.out.println("Total: " + totalPrice);
            customer.addReservation(this);
        } else {
            System.out.println("You can't book the room, it is not available!");
            FileManager.writeReservation(this);

        }
    }

    @Override
    public boolean isBooked() {
        return isBooked;
    }
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }


    public void cancel() {
        if (isBooked && !isCancelled) {
            this.isCancelled = true;
            this.isBooked = false;
            room.setAvailable(true);
            System.out.println("Reservation " + reservationId + " cancelled!");
        } else {
            System.out.println("Cannot cancel - not booked");
        }
    }

    public void makePayment(Payment payment) {
        if (payment.processPayment()) {
            this.payment = payment;
            payment.completePayment();
            System.out.println("Payment completed for reservation " + reservationId);
        } else {
            System.out.println("Payment failed");
        }
    }
}
