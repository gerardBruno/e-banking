package com.bygit.e_banking.exceptions;

public class BalanceNotSufficientException extends  Exception{
    public BalanceNotSufficientException(String message) {
        super(message);

    }
}
