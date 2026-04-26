public class CreditCardPayment extends Payment {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;

    public CreditCardPayment(double amount, String paymentId, String paymentDate,
                             String cardNumber, String cardHolderName, String expiryDate) {
        super(amount, paymentId, paymentDate);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean processPayment() {
        System.out.println("Processing credit card payment for " + cardHolderName + "...");

        try { //validates 16 digits
            if (cardNumber.length() < 16) {
                throw new IllegalArgumentException("Card number is not valid");
                }
            if(cardHolderName==null){
               throw new IllegalArgumentException("Card Holder Name is null");
                }
            if(cardHolderName.matches(".*\\d.*")) { // if it has a number
                throw new IllegalArgumentException("Name must contain only letters.");
            }

            if(expiryDate==null){
                    throw new IllegalArgumentException("Expiry Date can't be empty");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return false; //if there's a mistake
        }
        return true; // if its all true
    }


    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

}
