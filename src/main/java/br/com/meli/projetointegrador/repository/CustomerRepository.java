package br.com.meli.projetointegrador.repository;

import br.com.meli.projetointegrador.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByUser_Id(Long id);
}
