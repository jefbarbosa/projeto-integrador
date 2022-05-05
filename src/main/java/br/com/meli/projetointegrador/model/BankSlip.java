package br.com.meli.projetointegrador.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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
