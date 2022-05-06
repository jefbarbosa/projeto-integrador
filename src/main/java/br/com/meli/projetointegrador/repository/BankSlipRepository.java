package br.com.meli.projetointegrador.repository;

import br.com.meli.projetointegrador.dto.BankSlipResult;
import br.com.meli.projetointegrador.model.BankSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BankSlipRepository extends JpaRepository<BankSlip, Long> {
    Optional<BankSlip> findByOrderStatus_Id(Long id);

    Optional<BankSlip> findByOrderStatus_IdAndEmailEquals(Long id, String email);

    @Query(value = "select ca.total_cart as total, ca.order_date as date, u.name, u.email, u.cpf from cart ca inner join order_status o on o.id=ca.order_status_id " +
            "inner join customer ct on ct.id=ca.customer_id inner join user u on u.id=ct.user_id where o.status_code='PURCHASE' and o.id=?1 and ct.id=?2", nativeQuery = true)
    BankSlipResult getCustomerAndCartDetailsByOrderId(Long orderId, Long customerId);

}