package br.com.meli.projetointegrador.controller;


import br.com.meli.projetointegrador.dto.BankSlipResult;
import br.com.meli.projetointegrador.dto.CartDTO;
import br.com.meli.projetointegrador.model.BankSlip;
import br.com.meli.projetointegrador.model.Cart;
import br.com.meli.projetointegrador.service.BankSlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
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

import javax.xml.transform.TransformerException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fresh-products/bank-slip")
public class BankSlipController {

    @Autowired
    private BankSlipService bankSlipService;

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

//    @GetMapping("/barcode")
//    public ResponseEntity<BufferedImage> getBarcode(@RequestParam String barcode) throws Exception {
//        return new ResponseEntity<>(bankSlipService.generateEAN13BarcodeImage(barcode), HttpStatus.OK);
//    }

    @GetMapping("/barcode")
    public ResponseEntity<String> getBarcode(@RequestParam String barcode) throws Exception {
        return new ResponseEntity<>(bankSlipService.generateBarcodeB64String(barcode), HttpStatus.OK);
    }

    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    @GetMapping("/generate")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<byte[]> getPdf(@RequestParam Long orderId) throws Exception {

//        BankSlipResult bankSlipResult = bankSlipService.getBankSlip(orderId);

        byte[] pdfBytes = bankSlipService.generateBankSlipPdf(orderId);
//        InputStream targetStream = new ByteArrayInputStream(pdfBytes);

//        File file = new File(fileName);
//        FileUtils.writeByteArrayToFile(file, pdfBytes); //org.apache.commons.io.FileUtils
//        InputStreamResource resource = new InputStreamResource(targetStream);
        MediaType mediaType = MediaType.parseMediaType("application/pdf");

        HttpHeaders headers = new HttpHeaders();
//        headers.add("content-disposition", "attachment; filename=" + "XXX.pdf");
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add("content-disposition", "inline;filename=" + "XXX.pdf");


//        return ResponseEntity.ok()
//                // Content-Disposition
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "XXX.pdf")
//                // Content-Type
//                .contentType(mediaType)
//                // Contet-Length
////                .contentLength(file.length()) //length
//                .body(resource);

        return new ResponseEntity<>(
                pdfBytes, headers, HttpStatus.OK);
    }
}
