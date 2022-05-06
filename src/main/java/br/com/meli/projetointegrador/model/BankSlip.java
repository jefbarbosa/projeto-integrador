package br.com.meli.projetointegrador.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Classe de com modelo de informações básicas de Bank Slip(Boleto),
 * baseada em informações do usuário relacionado ao Customer no banco de dados
 * @author Jeferson Barbosa Sousa
 * */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_slip")
public class BankSlip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double total;
    private String date;
    private String name;
    private String email;
    private String cpf;

    @JsonIgnore
    @OneToOne
    private OrderStatus orderStatus;

    public BankSlip(Double total, String date, String name, String email, String cpf, OrderStatus orderStatus) {
        this.total = total;
        this.date = date;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.orderStatus = orderStatus;
    }
}
