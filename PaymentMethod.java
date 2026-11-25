package model;

import exception.InsufficientBalanceException;

/** Interface: the payment workflow depends only on this, not on a class. */
public interface PaymentMethod {
    String getName();

    void pay(Client payer, double amount) throws InsufficientBalanceException;
}
