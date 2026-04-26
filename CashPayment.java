public class CashPayment extends Payment {

    private double cashReceived;
    private double change;

    public CashPayment(double amount, String paymentId, String paymentDate,
                       double cashReceived) {
        super(amount, paymentId, paymentDate);
        this.cashReceived = cashReceived;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public double getChange() {
        return change; 
    }

    @Override
    public boolean processPayment() {

        try {
            if (getAmount() <= 0) { 
                throw new IllegalArgumentException("Amount isn't valid");
            }

            if (cashReceived <= 0) { 
                throw new IllegalArgumentException("Cash received isn't valid");
            }

            if (cashReceived < getAmount()) {
                throw new IllegalArgumentException("Not enough cash");
            }

            change = cashReceived - getAmount(); // calculate change
            System.out.println("Cash payment successful");
            System.out.println("Change: " + change);

            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("Cash payment failed: " + e.getMessage());
            return false;
        }
    }
}



