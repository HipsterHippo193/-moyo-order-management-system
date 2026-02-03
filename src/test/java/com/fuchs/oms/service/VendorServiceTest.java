package com.fuchs.oms.service;

import com.fuchs.oms.dto.PriceUpdateRequest;
import com.fuchs.oms.dto.PriceUpdateResponse;
import com.fuchs.oms.dto.StockUpdateRequest;
import com.fuchs.oms.dto.StockUpdateResponse;
import com.fuchs.oms.dto.VendorProductResponse;
import com.fuchs.oms.exception.InsufficientStockException;
import com.fuchs.oms.exception.ResourceNotFoundException;
import com.fuchs.oms.model.Product;
import com.fuchs.oms.model.Vendor;
import com.fuchs.oms.model.VendorProduct;
import com.fuchs.oms.repository.VendorProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {

    @Mock
    private VendorProductRepository vendorProductRepository;

    @InjectMocks
    private VendorService vendorService;

    private Vendor vendor;
    private Product product;
    private VendorProduct vendorProduct;

    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setId(1L);
        vendor.setUsername("vendor-a");
        vendor.setName("Vendor Alpha");

        product = new Product();
        product.setId(1L);
        product.setProductCode("widget-001");
        product.setName("Widget");
        product.setDescription("Standard widget");

        vendorProduct = new VendorProduct();
        vendorProduct.setId(1L);
        vendorProduct.setVendor(vendor);
        vendorProduct.setProduct(product);
        vendorProduct.setPrice(new BigDecimal("50.00"));
        vendorProduct.setStock(100);
    }

    @Test
    void getVendorProducts_returnsCorrectDTOList() {
        when(vendorProductRepository.findByVendorIdWithProduct(1L))
            .thenReturn(Arrays.asList(vendorProduct));

        List<VendorProductResponse> result = vendorService.getVendorProducts(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        assertThat(result.get(0).getProductCode()).isEqualTo("widget-001");
        assertThat(result.get(0).getName()).isEqualTo("Widget");
        assertThat(result.get(0).getPrice()).isEqualTo(new BigDecimal("50.00"));
        assertThat(result.get(0).getStock()).isEqualTo(100);

        verify(vendorProductRepository).findByVendorIdWithProduct(1L);
    }

    @Test
    void getVendorProducts_returnsEmptyListWhenNoProducts() {
        when(vendorProductRepository.findByVendorIdWithProduct(1L))
            .thenReturn(Collections.emptyList());

        List<VendorProductResponse> result = vendorService.getVendorProducts(1L);

        assertThat(result).isEmpty();
        verify(vendorProductRepository).findByVendorIdWithProduct(1L);
    }

    @Test
    void updatePrice_updatesAndReturnsResponse() {
        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("55.00"));
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L))
            .thenReturn(Optional.of(vendorProduct));
        when(vendorProductRepository.save(any(VendorProduct.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        PriceUpdateResponse result = vendorService.updatePrice(1L, 1L, request);

        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductCode()).isEqualTo("widget-001");
        assertThat(result.getProductName()).isEqualTo("Widget");
        assertThat(result.getNewPrice()).isEqualTo(new BigDecimal("55.00"));
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(vendorProductRepository).findByVendorIdAndProductIdWithProduct(1L, 1L);
        verify(vendorProductRepository).save(vendorProduct);
        assertThat(vendorProduct.getPrice()).isEqualTo(new BigDecimal("55.00"));
    }

    @Test
    void updatePrice_throwsWhenProductNotFound() {
        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("55.00"));
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 999L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendorService.updatePrice(1L, 999L, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Product not found for vendor")
            .hasMessageContaining("vendorId=1")
            .hasMessageContaining("productId=999");

        verify(vendorProductRepository).findByVendorIdAndProductIdWithProduct(1L, 999L);
        verify(vendorProductRepository, never()).save(any());
    }

    @Test
    void getVendorProducts_returnsMultipleProducts() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductCode("gadget-002");
        product2.setName("Gadget");

        VendorProduct vendorProduct2 = new VendorProduct();
        vendorProduct2.setId(2L);
        vendorProduct2.setVendor(vendor);
        vendorProduct2.setProduct(product2);
        vendorProduct2.setPrice(new BigDecimal("75.00"));
        vendorProduct2.setStock(50);

        when(vendorProductRepository.findByVendorIdWithProduct(1L))
            .thenReturn(Arrays.asList(vendorProduct, vendorProduct2));

        List<VendorProductResponse> result = vendorService.getVendorProducts(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        assertThat(result.get(1).getProductId()).isEqualTo(2L);
    }

    @Test
    void updateStock_updatesAndReturnsResponse() {
        StockUpdateRequest request = new StockUpdateRequest(150);
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L))
            .thenReturn(Optional.of(vendorProduct));
        when(vendorProductRepository.save(any(VendorProduct.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        StockUpdateResponse result = vendorService.updateStock(1L, 1L, request);

        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getProductCode()).isEqualTo("widget-001");
        assertThat(result.getProductName()).isEqualTo("Widget");
        assertThat(result.getNewStock()).isEqualTo(150);
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(vendorProductRepository).findByVendorIdAndProductIdWithProduct(1L, 1L);
        verify(vendorProductRepository).save(vendorProduct);
        assertThat(vendorProduct.getStock()).isEqualTo(150);
    }

    @Test
    void updateStock_throwsWhenProductNotFound() {
        StockUpdateRequest request = new StockUpdateRequest(150);
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 999L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendorService.updateStock(1L, 999L, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Product not found for vendor")
            .hasMessageContaining("vendorId=1")
            .hasMessageContaining("productId=999");

        verify(vendorProductRepository).findByVendorIdAndProductIdWithProduct(1L, 999L);
        verify(vendorProductRepository, never()).save(any());
    }

    @Test
    void decrementStock_withSufficientStock_reducesStockByQuantity() {
        vendorProduct.setStock(50);
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L))
            .thenReturn(Optional.of(vendorProduct));
        when(vendorProductRepository.save(any(VendorProduct.class)))
            .thenReturn(vendorProduct);

        vendorService.decrementStock(1L, 1L, 10);

        assertThat(vendorProduct.getStock()).isEqualTo(40);
        verify(vendorProductRepository).save(vendorProduct);
    }

    @Test
    void decrementStock_persistsUpdatedStockToDatabase() {
        vendorProduct.setStock(100);
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L))
            .thenReturn(Optional.of(vendorProduct));
        when(vendorProductRepository.save(any(VendorProduct.class)))
            .thenReturn(vendorProduct);

        vendorService.decrementStock(1L, 1L, 25);

        verify(vendorProductRepository).save(vendorProduct);
        assertThat(vendorProduct.getStock()).isEqualTo(75);
    }

    @Test
    void decrementStock_withInsufficientStock_throwsInsufficientStockException() {
        vendorProduct.setStock(5);
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L))
            .thenReturn(Optional.of(vendorProduct));

        assertThatThrownBy(() -> vendorService.decrementStock(1L, 1L, 10))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock")
            .hasMessageContaining("available=5")
            .hasMessageContaining("requested=10");

        verify(vendorProductRepository, never()).save(any());
    }

    @Test
    void decrementStock_withInsufficientStock_leavesStockUnchanged() {
        vendorProduct.setStock(5);
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L))
            .thenReturn(Optional.of(vendorProduct));

        try {
            vendorService.decrementStock(1L, 1L, 10);
        } catch (InsufficientStockException e) {
            // expected
        }

        assertThat(vendorProduct.getStock()).isEqualTo(5);
        verify(vendorProductRepository, never()).save(any());
    }

    @Test
    void decrementStock_withNonExistentProduct_throwsResourceNotFoundException() {
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 999L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendorService.decrementStock(1L, 999L, 10))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Product not found for vendor")
            .hasMessageContaining("vendorId=1")
            .hasMessageContaining("productId=999");

        verify(vendorProductRepository, never()).save(any());
    }

    @Test
    void decrementStock_withExactStockAmount_reducesToZero() {
        vendorProduct.setStock(10);
        when(vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L))
            .thenReturn(Optional.of(vendorProduct));
        when(vendorProductRepository.save(any(VendorProduct.class)))
            .thenReturn(vendorProduct);

        vendorService.decrementStock(1L, 1L, 10);

        assertThat(vendorProduct.getStock()).isEqualTo(0);
        verify(vendorProductRepository).save(vendorProduct);
    }
}
