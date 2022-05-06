package br.com.meli.projetointegrador.dto;

public interface BankSlipResult {
    Double getTotal();
    String getDate();
    String getName();
    String getEmail();
    String getCpf();
}