package br.com.meli.projetointegrador.dto;

import br.com.meli.projetointegrador.model.Cart;
import br.com.meli.projetointegrador.model.Customer;
import br.com.meli.projetointegrador.model.Item;
import br.com.meli.projetointegrador.model.OrderStatus;
import br.com.meli.projetointegrador.service.AdvertisementService;
import br.com.meli.projetointegrador.service.CustomerService;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartDTO {

    @NotNull(message = "OrderDate missing.")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate orderDate;

    @NotNull(message = "CustomerId missing.")
    private Long customerId;

    @NotNull(message = "OrderStatus missing.")
    private @Valid OrderStatus orderStatus;
    @NotNull(message = "Items missing.")
    private List<@Valid ItemDTO> items;

    private BigDecimal totalValue;

    public static Cart map(CartDTO cartDTO, CustomerService customerService, AdvertisementService advertisementService) {
        Customer customer = customerService.findById(cartDTO.getCustomerId());
        List<Item> items = cartDTO.getItems().stream().map(item -> ItemDTO.map(item, advertisementService)).collect(Collectors.toList());

        return Cart.builder()
                .customer(customer)
                .orderDate(cartDTO.getOrderDate())
                .orderStatus(cartDTO.getOrderStatus())
                .items(items)
                .build();
    }

    public CartDTO(LocalDate orderDate, OrderStatus orderStatus, BigDecimal totalValue) {
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.totalValue = totalValue;
    }

    public static CartDTO basicInfo(Cart cart) {
        return new CartDTO(cart.getOrderDate(), cart.getOrderStatus(), cart.getTotalCart());
    }
}
