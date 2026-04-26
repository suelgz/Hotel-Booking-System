public class StandardRoom extends Room{

    public StandardRoom(String roomNumber, String capacity, double price, boolean isAvailable ) {
        super(roomNumber, capacity, price, isAvailable);
    }

    @Override
   public void printAvailable() {
        System.out.println("Standard Room availability: " + isAvailable());
    }
    @Override
    public double calculatePrice(int numberOfNights) {
        return getPrice() * numberOfNights;
    }

    @Override
    public String toString() {
        return "StandardRoom - Room: " + getRoomNumber() + ", Price: " + getPrice() + ", Available: " + isAvailable();
    }
}

