public class SuiteRoom extends Room {

    private int luxuryFee;
    private String suiteLevel;
    private boolean hasLivingRoom;


    public SuiteRoom(String roomNumber, String capacity, double price, boolean isAvailable,
                     int luxuryFee, String suiteLevel, boolean hasLivingRoom) {

        super(roomNumber, capacity, price, isAvailable);
        this.luxuryFee = luxuryFee;
        this.suiteLevel = suiteLevel;
        this.hasLivingRoom = hasLivingRoom;
    }
    public double getLuxuryFee() {
        return luxuryFee;
    }

    public String getSuiteLevel() {
        return suiteLevel;
    }

    public boolean hasLivingRoom() {
        return hasLivingRoom;
    }

    @Override
    public void printAvailable() {
        System.out.println("Suite Room availability: " + isAvailable());
    }

    @Override
    public double calculatePrice(int numberOfNights) {
        double totalPrice = (getPrice() + luxuryFee) * numberOfNights;
        return totalPrice;
    }
    @Override
    public String toString() {
        return "SuiteRoom " + getRoomNumber() + " - " + suiteLevel + " Suite, Price: " + getPrice() + ", Luxury Fee: $" + luxuryFee + ", Living Room: " + (hasLivingRoom ? "Yes" : "No") + ", Available: " + (isAvailable() ? "Yes" : "No");
    }
}

