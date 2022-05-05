package br.com.meli.projetointegrador.exception.handler;

import br.com.meli.projetointegrador.dto.ErrorDTO;
import br.com.meli.projetointegrador.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler(InexistentInboundOrderException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentInboundOrderException(InexistentInboundOrderException ex){
        ErrorDTO error = new ErrorDTO("InexistentInboundOrderException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InexistentBatchException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentBatchExceptionException(InexistentBatchException ex){
        ErrorDTO error = new ErrorDTO("InexistentBatchException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InexistentProductException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentProductException(InexistentProductException ex){
        ErrorDTO error = new ErrorDTO("InexistentProductException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundProductException.class)
    protected ResponseEntity<ErrorDTO> handleNotFoundProductException(NotFoundProductException ex){
        ErrorDTO error = new ErrorDTO("NotFoundProductException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InexistentSectionException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentSectionException(InexistentSectionException ex){
        ErrorDTO error = new ErrorDTO("InexistentSectionException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InexistentWarehouseException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentWarehouseException(InexistentWarehouseException ex){
        ErrorDTO error = new ErrorDTO("InexistentWarehouseException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SectionNotMatchWithBatchCategoryException.class)
    protected ResponseEntity<ErrorDTO> handleSectionNotMatchWithBatchCategoryException(SectionNotMatchWithBatchCategoryException ex){
        ErrorDTO error = new ErrorDTO("SectionNotMatchWithBatchCategoryException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SectionUnavailableSpaceException.class)
    protected ResponseEntity<ErrorDTO> handleSectionUnavailableSpaceException(SectionUnavailableSpaceException ex){
        ErrorDTO error = new ErrorDTO("SectionUnavailableSpaceException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StockManagerNotMatchWithWarehouseException.class)
    protected ResponseEntity<ErrorDTO> handleStockManagerNotMatchWithWarehouseException(StockManagerNotMatchWithWarehouseException ex){
        ErrorDTO error = new ErrorDTO("StockManagerNotMatchWithWarehouseException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SectionNotMatchWithWarehouseException.class)
    protected ResponseEntity<ErrorDTO> handleSectionNotMatchWithWarehouseException(SectionNotMatchWithWarehouseException ex){
        ErrorDTO error = new ErrorDTO("SectionNotMatchWithWarehouseException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InexistentStockManagerException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentStockManagerException(InexistentStockManagerException ex) {
        ErrorDTO error = new ErrorDTO("InexistentStockManagerException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InexistentAdvertisementException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentAdvertisementException(InexistentAdvertisementException ex){
        ErrorDTO error = new ErrorDTO("InexistentAdvertisementException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InexistentCustomerException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentCustomerException(InexistentCustomerException ex){
        ErrorDTO error = new ErrorDTO("InexistentCustomerException", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductHasNotBatchesAvailableToPurchaseException.class)
    protected ResponseEntity<ErrorDTO> handleProductHasNotBatchesAvailableToPurchase(ProductHasNotBatchesAvailableToPurchaseException ex){
        ErrorDTO error = new ErrorDTO("ProductHasNotBatchesAvailableToPurchase", "The following items has not enough stock to purchase: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductHasNotEnoughStockException.class)
    protected ResponseEntity<ErrorDTO> handleProductHasNotEnoughStockException(ProductHasNotEnoughStockException ex){
        ErrorDTO error = new ErrorDTO("ProductHasNotEnoughStockException", "The following items has not enough stock to purchase: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        ErrorDTO error = new ErrorDTO("MethodArgumentNotValidException",  ex.getBindingResult().getFieldError().getDefaultMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
        ErrorDTO error = new ErrorDTO("HttpMessageNotReadableException",  ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyProductListException.class)
    protected ResponseEntity<ErrorDTO> handleEmptyProductListException(EmptyProductListException ex){
        ErrorDTO error = new ErrorDTO("EmptyProductListException",  ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InexistentCartException.class)
    protected ResponseEntity<ErrorDTO> handleInexistentCartException(InexistentCartException ex){
        ErrorDTO error = new ErrorDTO("InexistentCartException",  ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatusCodeIncorrectException.class)
    protected ResponseEntity<ErrorDTO> handleStatusCodeIncorrectException(StatusCodeIncorrectException ex){
        ErrorDTO error = new ErrorDTO("StatusCodeIncorrectException",  ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserHasNoPurchaseException.class)
    protected ResponseEntity<ErrorDTO> handleUserHasNoPurchaseException(UserHasNoPurchaseException ex){
        ErrorDTO error = new ErrorDTO("UserHasNoPurchaseException",  ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserHasNoOrder.class)
    protected ResponseEntity<ErrorDTO> handleUserHasNoOrder(UserHasNoOrder ex){
        ErrorDTO error = new ErrorDTO("UserHasNoOrder",  ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
