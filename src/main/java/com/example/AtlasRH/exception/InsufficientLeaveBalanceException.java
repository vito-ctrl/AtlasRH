package com.example.AtlasRH.exception;

public class InsufficientLeaveBalanceException extends BusinessRuleException {

    public InsufficientLeaveBalanceException(String message) {
        super(message);
    }

    public InsufficientLeaveBalanceException(int soldeRestant, int joursDemandes) {
        super(String.format("Solde de congés insuffisant : solde restant = %d jour(s), demandé = %d jour(s).",
                soldeRestant, joursDemandes));
    }
}
