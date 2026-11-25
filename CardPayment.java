package model;

import exception.InsufficientBalanceException;

public class CardPayment implements PaymentMethod {
    private final String last4;

    public CardPayment(String last4) { this.last4 = last4; }

    @Override
    public String getName() { return "Card ending " + last4; }

    @Override
    public void pay(Client payer, double amount) throws InsufficientBalanceException {
        if (payer.getBalance() < amount)
            throw new InsufficientBalanceException("Card payment failed: balance Rs."
                + payer.getBalance() + " is less than Rs." + amount);
        payer.withdraw(amount);
    }
}
