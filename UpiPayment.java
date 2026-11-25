package model;

import exception.InsufficientBalanceException;

public class UpiPayment implements PaymentMethod {
    private final String upiId;

    public UpiPayment(String upiId) { this.upiId = upiId; }

    @Override
    public String getName() { return "UPI (" + upiId + ")"; }

    @Override
    public void pay(Client payer, double amount) throws InsufficientBalanceException {
        if (payer.getBalance() < amount)
            throw new InsufficientBalanceException("UPI payment failed: balance Rs."
                + payer.getBalance() + " is less than Rs." + amount);
        payer.withdraw(amount);
    }
}
