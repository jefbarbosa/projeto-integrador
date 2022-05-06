package br.com.meli.projetointegrador.exception;

public class UserHasNoOrder extends RuntimeException {
    public UserHasNoOrder(String message) {
        super(message);
    }
}
