package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.model.Customer;

import java.util.Optional;

public interface CustomerService {
    Customer findById(Long id);
    Optional<Customer> findCustomerByUser_Id(Long id);
}
