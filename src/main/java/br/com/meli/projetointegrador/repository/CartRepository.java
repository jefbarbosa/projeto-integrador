package br.com.meli.projetointegrador.repository;

import br.com.meli.projetointegrador.model.Cart;
import br.com.meli.projetointegrador.model.StatusCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> getAllByCustomerIdAndOrderStatus_StatusCode(Long customerId, StatusCode statusCode);
}
