package com.moyo.oms.service;

import com.moyo.oms.dto.ProductRequest;
import com.moyo.oms.dto.ProductResponse;
import com.moyo.oms.exception.CategoryNotFoundException;
import com.moyo.oms.exception.ProductCodeAlreadyExistsException;
import com.moyo.oms.exception.ProductInUseException;
import com.moyo.oms.exception.ProductNotFoundException;
import com.moyo.oms.model.Category;
import com.moyo.oms.model.Product;
import com.moyo.oms.repository.CategoryRepository;
import com.moyo.oms.repository.OrderRepository;
import com.moyo.oms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllWithCategory().stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return toProductResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.findByProductCode(request.getProductCode()).isPresent()) {
            throw new ProductCodeAlreadyExistsException(request.getProductCode());
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setProductCode(request.getProductCode());
        product.setDescription(request.getDescription());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        return toProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        // Check if product code is being changed to one that already exists
        if (!product.getProductCode().equals(request.getProductCode())) {
            if (productRepository.findByProductCode(request.getProductCode()).isPresent()) {
                throw new ProductCodeAlreadyExistsException(request.getProductCode());
            }
        }

        product.setName(request.getName());
        product.setProductCode(request.getProductCode());
        product.setDescription(request.getDescription());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        Product savedProduct = productRepository.save(product);
        return toProductResponse(savedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (orderRepository.existsByProductId(id)) {
            throw new ProductInUseException(product.getName());
        }

        productRepository.delete(product);
    }

    private ProductResponse toProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setProductCode(product.getProductCode());
        response.setName(product.getName());
        response.setDescription(product.getDescription());

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        if (product.getCreatedAt() != null) {
            response.setCreatedAt(product.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        }

        return response;
    }
}
