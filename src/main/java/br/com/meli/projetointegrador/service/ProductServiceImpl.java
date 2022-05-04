package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.dto.ProductByBatchResponse;
import br.com.meli.projetointegrador.dto.ProductDTOi;
import br.com.meli.projetointegrador.exception.InexistentProductException;
import br.com.meli.projetointegrador.model.Batch;
import br.com.meli.projetointegrador.model.Product;
import br.com.meli.projetointegrador.repository.BatchRepository;
import br.com.meli.projetointegrador.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
/**
 * Classe de implementação de ProductService responsável por processar dados de Product.
 * Possui serviços para bucar produtos, com especificidades diversas.
 * @author Jeferson Barbosa Souza
 * @author Lucas Troleiz Lopes
 */
@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;
    private BatchRepository batchRepository;
    private BatchService batchService;


    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new InexistentProductException("Product " + id + " does not exists!"));
    }

    @Override
    public List<ProductByBatchResponse> getAllProductThatHaveBatch(Long id, String orderBy) {

        List<ProductByBatchResponse> productByBatchResponseList = new ArrayList<>();

        switch (orderBy) {
            case "L":
                return batchRepository.getAllProductThatHaveBatch(id);
            case "C":

                return batchRepository.getAllProductThatHaveBatchQuantity(id);
            case "F":
                return batchRepository.getAllProductThatHaveBatchExpiration(id);
            default:
                return productByBatchResponseList;
        }
    }

    @Override
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    @Override
    public List<ProductDTOi> findAllByBatchListExists() {
        return productRepository.findAllByBatchListExists();
    }

    @Override
    public List<ProductDTOi> findAllByBatchListExistsBySection(String category) {
        return productRepository.findAllByBatchListExistsBySection(category);
    }

    @Override
    public Integer getTotalQuantity(Long id) {
        List<Batch> batches = batchService.getBatchesWithExpirationDateGreaterThan3Weeks(id);
        return batches.stream().reduce(0, (acc, nextBatch) -> acc + nextBatch.getCurrentQuantity(), Integer::sum);
    }
}
