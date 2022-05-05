package br.com.meli.projetointegrador.controller;


import br.com.meli.projetointegrador.dto.BankSlipResult;
import br.com.meli.projetointegrador.dto.CartDTO;
import br.com.meli.projetointegrador.model.Cart;
import br.com.meli.projetointegrador.service.BankSlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fresh-products/bank-slip")
public class BankSlipController {

    private final String boletoName = "boleto.pdf";

    @Autowired
    private BankSlipService bankSlipService;

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    @GetMapping("/find-purchases-by-customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<List<CartDTO>> getBankSlip() {
        List<Cart> carts = bankSlipService.getPurchasedCartsByCustomer();
        List<CartDTO> cartDTOList = carts.stream().map(CartDTO::basicInfo).collect(Collectors.toList());
        return new ResponseEntity<>(cartDTOList, HttpStatus.OK);
    }

    @GetMapping("/find-by-order")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<BankSlipResult> getBankSlip(@RequestParam Long orderId) {
        return new ResponseEntity<>(bankSlipService.getBankSlip(orderId), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping(value = "/generate-barcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> getBarcode(@RequestParam Long orderId) throws Exception {
        return new ResponseEntity<>(bankSlipService.generateBarcodeB64(orderId), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping(value = "/generate-qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> getQRcode(@RequestParam Long orderId) throws Exception {
        return new ResponseEntity<>(bankSlipService.generateQRcodeB64(orderId), HttpStatus.OK);
    }


    @GetMapping("/generate")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<byte[]> getBankSlipPdf(@RequestParam Long orderId) throws Exception {

        byte[] pdfBytes = bankSlipService.generateBankSlipPdf(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add("content-disposition", "inline;filename=" + boletoName);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
