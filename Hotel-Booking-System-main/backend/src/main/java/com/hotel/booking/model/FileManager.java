import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileManager {

    // file name where reservation data is stored
    private static final String FILE_NAME = "reservations.txt";


    public static void writeReservation(Reservation r) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {

            writer.write(
                    r.getReservationId() + "," +
                            r.getCustomer().getName() + "," +
                            r.getRoom().getRoomNumber() + "," +
                            r.getCheckInDate() + "," +
                            r.getCheckOutDate() + "," +
                            r.getTotalPrice()
            );

            // moves to the next line after writing a reservation
            writer.write(System.lineSeparator());

        } catch (IOException e) {
            System.out.println("An error occurred while writing the file");
        }
    }
    // reads all res. from the file , each line printed
    public static void readReservations() {
        try (BufferedReader reader = new BufferedReader(new FileReader("reservations.txt"))) {
            String line;

            System.out.println("Reservations from file");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.out.println("An error occurred while reading the file");
        }
    }
}
