package br.com.meli.projetointegrador.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BankSlipResultImpl implements  BankSlipResult{
    private Double total;
    private String date;
    private String name;
    private String email;
    private String cpf;

}