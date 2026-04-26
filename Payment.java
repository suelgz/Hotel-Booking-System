public abstract class Payment {
    private double amount;
    private String paymentId;
    private String paymentDate;
    private boolean isCompleted;

    public Payment (double amount, String paymentId, String paymentDate ) {
        this.amount = amount;
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.isCompleted = false;
    }


    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public String getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
   public abstract boolean processPayment();

    public void completePayment() {
        this.isCompleted = true;
        System.out.println("Payment " + paymentId + " completed!");
    }

    @Override
    public String toString() {
        return "Payment" + paymentId + "\nPaymentDate" + paymentDate + "\nAmount:" + amount;
    }

}

