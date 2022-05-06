package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.model.*;
import br.com.meli.projetointegrador.repository.CartRepository;
import br.com.meli.projetointegrador.security.services.UserDetailsImpl;
import br.com.meli.projetointegrador.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartServiceTest {

    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private BatchService batchService;

    @Mock
    private ItemService itemService;

    @Mock
    private CustomerService customerService;

    @Mock
    private OrderStatusService orderStatusService;

    @BeforeEach
    private void initializeAdvertisementService() {
        MockitoAnnotations.openMocks(this);
        this.cartService = new CartServiceImpl(cartRepository, productService, batchService, itemService, customerService, orderStatusService);
    }

    private void initializeAuthentication(){
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when((UserDetailsImpl) authentication.getPrincipal()).thenReturn(UserDetailsImpl.build(new User(1L, "Igor", "123.456.789-10", "igor@gmail.com", "igor_sn", "abcd1234", Set.of(new Role(1, ERole.ROLE_STOCK_MANAGER)))));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void findByIdTest() {
        Cart cart = new Cart(1L, LocalDate.of(2022, 1, 1), new Customer(), BigDecimal.valueOf(200), new OrderStatus(), Arrays.asList(new Item(), new Item()));

        Mockito.when(cartRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(cart));

        assertEquals(1L, cartService.findById(1L).getId());
    }


    @Test
    public void saveTest() {

        initializeAuthentication();

        Product product = new Product(1L, "Produto 1", 100.0, 2.0, 2.0, Arrays.asList(new Batch(), new Batch()));
        Advertisement advertisement = new Advertisement(1L, "Advertisement 1", BigDecimal.valueOf(100), product, new Seller());
        Item item = new Item(1L, advertisement, new Cart(), 100);

        Cart cart = new Cart(1L, LocalDate.of(2022, 1, 1), new Customer(), BigDecimal.valueOf(0), new OrderStatus(1L, StatusCode.CART), Arrays.asList(item));

        item.setCart(cart);

        List<Batch> batchList = Arrays.asList(new Batch(), new Batch());
        Mockito.when(batchService.getBatchesWithExpirationDateGreaterThan3Weeks(Mockito.any())).thenReturn(batchList);
        Mockito.when(productService.getTotalQuantity(Mockito.any())).thenReturn(100);
        Mockito.when(orderStatusService.save(Mockito.any())).thenReturn(new OrderStatus());
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cart);
        Mockito.when(itemService.save(Mockito.any())).thenReturn(new ArrayList<>());

        assertEquals(BigDecimal.valueOf(10000), cartService.save(cart));
    }

    @Test
    public void updateCartToPurchaseTest() {

        Product product = new Product(1L, "Produto 1", 100.0, 2.0, 2.0, Arrays.asList(new Batch(), new Batch()));
        Advertisement advertisement = new Advertisement(1L, "Advertisement 1", BigDecimal.valueOf(100), product, new Seller());
        Item item = new Item(1L, advertisement, new Cart(), 100);

        Cart cart = new Cart(1L, LocalDate.of(2022, 1, 1), new Customer(), BigDecimal.valueOf(0), new OrderStatus(1L, StatusCode.PURCHASE), Arrays.asList(item));

        item.setCart(cart);

        List<Batch> batchList = Arrays.asList(new Batch(), new Batch());
        Mockito.when(batchService.getBatchesWithExpirationDateGreaterThan3Weeks(Mockito.any())).thenReturn(batchList);
        Mockito.when(productService.getTotalQuantity(Mockito.any())).thenReturn(100);
        Mockito.when(orderStatusService.save(Mockito.any())).thenReturn(new OrderStatus());
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cart);
        Mockito.when(cartRepository.findById(Mockito.any())).thenReturn(java.util.Optional.of(cart));
        Mockito.when(itemService.save(Mockito.any())).thenReturn(new ArrayList<>());

        assertEquals(cart.getOrderStatus().getStatusCode().name(), cartService.updateCartToPurchase(1L).getOrderStatus().getStatusCode().name());
    }
}
