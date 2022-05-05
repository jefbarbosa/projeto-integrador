package br.com.meli.projetointegrador.exception;

public class UserHasNoPurchaseException extends RuntimeException {
    public UserHasNoPurchaseException(String message) {
        super(message);
    }
}
