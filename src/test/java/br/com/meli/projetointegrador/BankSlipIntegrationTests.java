package br.com.meli.projetointegrador;

import br.com.meli.projetointegrador.dto.*;
import br.com.meli.projetointegrador.model.request.LoginRequest;
import br.com.meli.projetointegrador.model.response.JwtResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BankSlipIntegrationTests {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static boolean init = false;
    private static String STOCK_MANAGER_JWT = "";
    private static String CUSTOMER_JWT = "";
    private static String CUSTOMER_2_JWT = "";
    private static String CUSTOMER_3_JWT = "";


    private String getStandardInboundOrder_1() {
        return "{\n" +
                "    \"orderNumber\": 2,\n" +
                "    \"orderDate\": \"2020-01-05\",\n" +
                "    \"section\": {\n" +
                "        \"sectionCode\": 1,\n" +
                "        \"warehouseCode\": 1\n" +
                "    },\n" +
                "    \"batchStock\": [\n" +
                "        {\n" +
                "            \"productId\": 1,\n" +
                "            \"currentTemperature\": -1,\n" +
                "            \"minTemperature\": -10,\n" +
                "            \"initialQuantity\": 60,\n" +
                "            \"currentQuantity\": 60,\n" +
                "            \"manufacturingDate\": \"2022-10-10\",\n" +
                "            \"manufacturingTime\": \"2022-10-10T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-10-10\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"productId\": 2,\n" +
                "            \"currentTemperature\": -5,\n" +
                "            \"minTemperature\": -10,\n" +
                "            \"initialQuantity\": 60,\n" +
                "            \"currentQuantity\": 60,\n" +
                "            \"manufacturingDate\": \"2022-10-10\",\n" +
                "            \"manufacturingTime\": \"2022-10-10T00:00:00\",\n" +
                "            \"expirationDate\": \"2022-10-10\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }


    public String getPurchaseOrder_1(){
        return   "{\n" +
                "    \"orderDate\": \"2022-02-02\",\n" +
                "    \"customerId\": 1,\n" +
                "    \"orderStatus\": {\n" +
                "        \"statusCode\": \"CART\"\n" +
                "    },\n" +
                "    \"items\": [\n" +
                "        {\n" +
                "            \"advertisementId\": 1,\n" +
                "            \"quantity\": 2\n" +
                "        },\n" +
                "        {\n" +
                "            \"advertisementId\": 2,\n" +
                "            \"quantity\": 5\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    public String getPurchaseOrder_2() {
        return "{\n" +
                "    \"orderDate\": \"2022-02-01\",\n" +
                "    \"customerId\": 2,\n" +
                "    \"orderStatus\": {\n" +
                "        \"statusCode\": \"CART\"\n" +
                "    },\n" +
                "    \"items\": [\n" +
                "        {\n" +
                "            \"advertisementId\": 1,\n" +
                "            \"quantity\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"advertisementId\": 2,\n" +
                "            \"quantity\": 1\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    public String signUpStockManagerBody() {
        return "{\n" +
                "    \"name\" : \"stockmanagertest\",\n" +
                "    \"username\" : \"stockmanagertest\",\n" +
                "    \"email\" : \"stockmanagertest@teste.com.br\",\n" +
                "    \"cpf\" : \"000-000-000-07\",\n" +
                "    \"password\" : \"abcd1234\",\n" +
                "    \"warehouse_id\": 1,\n" +
                "    \"role\" : [\"manager\"]\n" +
                "}";
    }

    public String signUpCustomerBody_1() {
        return "{\n" +
                "    \"name\" : \"customertest\",\n" +
                "    \"username\" : \"customertest\",\n" +
                "    \"email\" : \"customertest@teste.com.br\",\n" +
                "    \"cpf\" : \"000-000-000-01\",\n" +
                "    \"password\" : \"abcd12345\",\n" +
                "    \"role\" : [\"customer\"]\n" +
                "}";
    }

    public String signUpCustomerBody_2() {
        return "{\n" +
                "    \"name\" : \"ctest\",\n" +
                "    \"username\" : \"ctest\",\n" +
                "    \"email\" : \"ctest@teste.com.br\",\n" +
                "    \"cpf\" : \"000-000-000-02\",\n" +
                "    \"password\" : \"abcd123456\",\n" +
                "    \"role\" : [\"customer\"]\n" +
                "}";
    }

    public String signUpCustomerBody_3() {
        return "{\n" +
                "    \"name\" : \"cust3\",\n" +
                "    \"username\" : \"cust3\",\n" +
                "    \"email\" : \"cust3@teste.com.br\",\n" +
                "    \"cpf\" : \"000-000-000-03\",\n" +
                "    \"password\" : \"abcd1234567\",\n" +
                "    \"role\" : [\"customer\"]\n" +
                "}";
    }

    public void signUpPost(ResultMatcher resultMatcher, String signUpDTO) throws Exception {

        mockmvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(signUpDTO))
                .andExpect(resultMatcher);

    }

    public String signInPost(LoginRequest loginRequest, ResultMatcher resultMatcher) throws Exception {

        MvcResult result = mockmvc.perform(post("/api/auth/signin")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(resultMatcher).andReturn();

        return result.getResponse().getContentAsString();

    }

    private String postInboundOrder(InboundOrderDTO inboundOrderDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/inboundorder")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(inboundOrderDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String postPurchaseOrder(CartDTO cartDTO, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(post("/api/v1/fresh-products/orders")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(cartDTO)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String putPurchase(Long cartId, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(put("/api/v1/fresh-products/orders/{cartId}", cartId)
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String getPurchasesByCustomer(ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/bank-slip/find-purchases-by-customer")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private String getBankSlipByOrder(Long orderId, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/bank-slip/find-by-order")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .param("orderId", String.valueOf(orderId)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse().getContentAsString();
    }

    private MockHttpServletResponse getBankSlipByOrderPDF(Long orderId, ResultMatcher resultMatcher, String jwt) throws Exception {

        MvcResult response = mockmvc.perform(get("/api/v1/fresh-products/bank-slip/generate")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .param("orderId", String.valueOf(orderId)))
                .andExpect(resultMatcher)
                .andReturn();

        return response.getResponse();
    }

    private void getBankSlipByOrderPDFBarcode(Long orderId, ResultMatcher resultMatcher, String jwt) throws Exception {

        mockmvc.perform(get("/api/v1/fresh-products/bank-slip/generate-barcode")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .param("orderId", String.valueOf(orderId)))
                .andExpect(resultMatcher);

    }

    private void getBankSlipByOrderPDFQRCode(Long orderId, ResultMatcher resultMatcher, String jwt) throws Exception {

        mockmvc.perform(get("/api/v1/fresh-products/bank-slip/generate-qrcode")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)
                .param("orderId", String.valueOf(orderId)))
                .andExpect(resultMatcher);
    }

    void makeInitialShopping() throws Exception {
        String inboundOrderString = getStandardInboundOrder_1();
        InboundOrderDTO inboundOrderDTO = objectMapper.readValue(inboundOrderString, new TypeReference<>() {});
        postInboundOrder(inboundOrderDTO, status().isCreated(), STOCK_MANAGER_JWT);

        String purchaseOrderString = getPurchaseOrder_1();
        CartDTO cartDTO = objectMapper.readValue(purchaseOrderString, new TypeReference<CartDTO>() {});
        postPurchaseOrder(cartDTO, status().isCreated(), CUSTOMER_JWT);
        putPurchase(1L, status().isOk(), CUSTOMER_JWT);

        purchaseOrderString = getPurchaseOrder_2();
        cartDTO = objectMapper.readValue(purchaseOrderString, new TypeReference<CartDTO>() {});
        postPurchaseOrder(cartDTO, status().isCreated(), CUSTOMER_2_JWT);
        putPurchase(2L, status().isOk(), CUSTOMER_2_JWT);
        postPurchaseOrder(cartDTO, status().isCreated(), CUSTOMER_2_JWT);
        putPurchase(3L, status().isOk(), CUSTOMER_2_JWT);
    }

    @BeforeEach
    void initialSetup() throws Exception {
        if (!init) {
            String signUpDTOStockManager = signUpStockManagerBody();
            signUpPost(status().isOk(), signUpDTOStockManager);

            String signUpDTOCustomer_1 = signUpCustomerBody_1();
            signUpPost(status().isOk(), signUpDTOCustomer_1);

            String signUpDTOCustomer_2 = signUpCustomerBody_2();
            signUpPost(status().isOk(), signUpDTOCustomer_2);

            String signUpDTOCustomer_3 = signUpCustomerBody_3();
            signUpPost(status().isOk(), signUpDTOCustomer_3);

            LoginRequest loginBody = new LoginRequest("stockmanagertest", "abcd1234");
            String signInResponse = signInPost(loginBody, status().isOk());
            JwtResponse jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {});
            STOCK_MANAGER_JWT = jwtResponse.getToken();

            loginBody = new LoginRequest("customertest", "abcd12345");
            signInResponse = signInPost(loginBody, status().isOk());
            jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {});
            CUSTOMER_JWT = jwtResponse.getToken();

            loginBody = new LoginRequest("ctest", "abcd123456");
            signInResponse = signInPost(loginBody, status().isOk());
            jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {});
            CUSTOMER_2_JWT = jwtResponse.getToken();

            loginBody = new LoginRequest("cust3", "abcd1234567");
            signInResponse = signInPost(loginBody, status().isOk());
            jwtResponse = objectMapper.readValue(signInResponse, new TypeReference<>() {});
            CUSTOMER_3_JWT = jwtResponse.getToken();

            makeInitialShopping();

            init = true;
        }
    }


    @Test
    void customerListAllPurchases() throws Exception {

        String allPurchases_1 = getPurchasesByCustomer(status().isOk(), CUSTOMER_JWT);
        List<CartDTO> cartDTOList_1 = objectMapper.readValue(allPurchases_1, new TypeReference<List<CartDTO>>() {});

        String allPurchases_2 = getPurchasesByCustomer(status().isOk(), CUSTOMER_2_JWT);
        List<CartDTO> cartDTOList_2 = objectMapper.readValue(allPurchases_2, new TypeReference<List<CartDTO>>() {});

        String allPurchases_3 = getPurchasesByCustomer(status().isNotFound(), CUSTOMER_3_JWT);
        ErrorDTO errorDTO = objectMapper.readValue(allPurchases_3, new TypeReference<>() {});

        assertAll(
                () -> assertEquals(cartDTOList_1.size(), 1),
                () -> assertEquals(cartDTOList_2.size(), 2),
                () -> assertEquals("This User has no Purchases!", errorDTO.getDescription())
        );
    }



    @Test
    void customerBankSlipPurchase() throws Exception {

        String allPurchases_1 = getBankSlipByOrder(1L, status().isOk(), CUSTOMER_JWT);
        BankSlipResultImpl bankSlipResult_1 = objectMapper.readValue(allPurchases_1, new TypeReference<>() {});

        String allPurchases_2 = getBankSlipByOrder(2L, status().isNotFound(), CUSTOMER_JWT);
        ErrorDTO errorDTO_2 = objectMapper.readValue(allPurchases_2, new TypeReference<>() {});

        String allPurchases_3 = getBankSlipByOrder(2L, status().isOk(), CUSTOMER_2_JWT);
        BankSlipResultImpl bankSlipResult_3 = objectMapper.readValue(allPurchases_3, new TypeReference<>() {});

        assertAll(
                () -> assertEquals("customertest", bankSlipResult_1.getName()),
                () -> assertEquals(210.0, bankSlipResult_1.getTotal()),
                () -> assertEquals("User has no Order with this Id!", errorDTO_2.getDescription()),
                () -> assertEquals("ctest", bankSlipResult_3.getName()),
                () -> assertEquals(60.0, bankSlipResult_3.getTotal())
        );
    }


    @Test
    void customerBankSlipPurchasePDF() throws Exception {
        MockHttpServletResponse pdfReturn = getBankSlipByOrderPDF(1L, status().isOk(), CUSTOMER_JWT);
        assertEquals("application/pdf", pdfReturn.getHeader("Content-Type"));
        assertEquals("inline;filename=boleto.pdf", pdfReturn.getHeader("content-disposition"));
    }

    @Test
    void customerBankSlipPurchasePDFBarcodeQRCode() throws Exception {
        getBankSlipByOrderPDFBarcode(1L, status().isOk(), CUSTOMER_JWT);
        getBankSlipByOrderPDFQRCode(1L, status().isOk(), CUSTOMER_JWT);
    }
}
