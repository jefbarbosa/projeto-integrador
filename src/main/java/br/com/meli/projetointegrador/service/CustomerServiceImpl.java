package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.exception.InexistentCustomerException;
import br.com.meli.projetointegrador.model.Customer;
import br.com.meli.projetointegrador.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new InexistentCustomerException("Customer " + id + " does not exists!"));
    }

    @Override
    public Optional<Customer> findCustomerByUser_Id(Long id) {
        return customerRepository.findCustomerByUser_Id(id);
    }
}
