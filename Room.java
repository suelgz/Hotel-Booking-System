public class Room {
    private String roomNumber;
    private int capacity;
    private double price;
    private boolean isAvailable;


    public Room(String roomNumber, int capacity, double price, boolean isAvailable) {
        try {
            if (capacity == null || capacity.isEmpty()) {
                throw new InvalidRoomDataException("Capacity can't be empty");
            }
            if (price < 0) {
                throw new InvalidRoomDataException("Price can't be negative");
            }

            this.roomNumber = roomNumber;
            this.capacity = capacity;
            this.price = price;
            this.isAvailable = isAvailable;

        } catch (InvalidRoomDataException e) {
            System.out.println("Room creation error: " + e.getMessage());
        }
    }

    public Room(String roomNumber, int capacity, double price) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.price = price;
        this.isAvailable = true;
    }

    public double calculatePrice(int numberOfNights) {
        return 0;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void printAvailable() {
        if (isAvailable) {
            System.out.println("Room is available");
        } else {
            System.out.println("Room is not available");
        }
    }


