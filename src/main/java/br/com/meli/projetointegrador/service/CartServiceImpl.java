package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.exception.InexistentCartException;
import br.com.meli.projetointegrador.model.*;
import br.com.meli.projetointegrador.repository.CartRepository;
import br.com.meli.projetointegrador.security.services.UserDetailsImpl;
import br.com.meli.projetointegrador.validator.OrderStatusCorrect;
import br.com.meli.projetointegrador.validator.ProductExpirationDateGreaterThan3Weeks;
import br.com.meli.projetointegrador.validator.ProductHasEnoughStock;
import br.com.meli.projetointegrador.validator.Validator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
/**
 * Classe de implementação de CartService responsável por processar dados de Cart.
 * Possui serviços para criar ordens de compra, edita-las e efetiva-las.
 * @author Igor de Souza Nogueira
 * @author Luis Felipe Floriano Olimpio
 * */
@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private CartRepository cartRepository;
    private ProductService productService;
    private BatchService batchService;
    private ItemService itemService;
    private CustomerService customerService;
    private OrderStatusService orderStatusService;

    @Override
    public BigDecimal save(Cart cart) {
        List<Validator> validators = Arrays.asList(
                new ProductExpirationDateGreaterThan3Weeks(cart.getItems(), batchService),
                new ProductHasEnoughStock(productService, cart.getItems()),
                new OrderStatusCorrect(cart.getOrderStatus().getStatusCode())
        );

        validators.forEach(Validator::validate);

        cart.setTotalCart(cart.getItems().stream().reduce(BigDecimal.valueOf(0), (acc, nextItem) -> acc.add(BigDecimal.valueOf(nextItem.getQuantity()).multiply(nextItem.getAdvertisement().getPrice())), BigDecimal::add));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long userId = userDetails.getId();

        Customer customer = customerService.findCustomerByUser_Id(userId).orElse(new Customer());
        cart.setCustomer(customer);

        orderStatusService.save(cart.getOrderStatus());

        Cart cartCreated = cartRepository.save(cart);

        cart.getItems().forEach(item -> item.setCart(cartCreated));

        itemService.save(cart.getItems());

        return cart.getTotalCart();
    }

    @Override
    public Cart findById(Long id) {
        return cartRepository.findById(id).orElseThrow(() -> new InexistentCartException("Cart " + id + " does not exists!"));
    }

    @Override
    public Cart updateCartToPurchase(Long id) {
        Cart cart = findById(id);

        List<Validator> validators = Arrays.asList(
                new ProductExpirationDateGreaterThan3Weeks(cart.getItems(), batchService),
                new ProductHasEnoughStock(productService, cart.getItems())
        );

        validators.forEach(Validator::validate);

        cart.getOrderStatus().setStatusCode(StatusCode.PURCHASE);

        batchService.takeOutProducts(cart.getItems());

        return cart;

    }
}
