package Wandera.E_Commerce.App.Services.ServiceImpl;

import Wandera.E_Commerce.App.Dtos.ProductRequest;
import Wandera.E_Commerce.App.Dtos.ProductResponse;
import Wandera.E_Commerce.App.Enum.Role;
import Wandera.E_Commerce.App.Entities.*;
import Wandera.E_Commerce.App.Exceptions.ResourceNotFoundException;
import Wandera.E_Commerce.App.Exceptions.UnauthorizedException;
import Wandera.E_Commerce.App.Mapper.ProductMapper;
import Wandera.E_Commerce.App.Repositories.CategoryRepository;
import Wandera.E_Commerce.App.Repositories.ProductRepository;
import Wandera.E_Commerce.App.Repositories.SellerProfileRepository;
import Wandera.E_Commerce.App.Repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final UserEntityRepository userEntityRepository;

    @CacheEvict(value = "ProductResponse", allEntries = true)
    public ProductResponse addProduct(ProductRequest productRequest) {


        // 1. Get logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new UnauthorizedException("No logged-in user found");

        }


        String email = auth.getName();

        // 2. Find user by email
        UserEntity user = userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Ensure user is a SELLER
        if (user.getRole() == Role.USER) {
            throw new UnauthorizedException("Only sellers can add products");
        }
        log.info(productRequest.getProductName()+" Is added successfully");

        // 4. Ensure seller already created a seller profile
        SellerProfile seller = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new UnauthorizedException("You must complete your seller profile before adding products"));

        // 5. Validate category
        Category category = categoryRepository.findByCategoryName(productRequest.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // 6. Build product entity
        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .productDescription(productRequest.getProductDescription())
                .productPrice(productRequest.getProductPrice())
                .categoryName(productRequest.getCategoryName())
                .productImageUrl(productRequest.getProductImageUrl())
                .backgroundColor(productRequest.getBackgroundColor())
                .category(category)
                .seller(seller)
                .build();

        // 7. Save product to database
        Product toEntity= productRepository.save(product);
       var savedProduct= productRepository.save(toEntity);

       return ProductMapper.toDto(savedProduct);

    }

    @Cacheable(value = "ProductResponse",key = "#productName")
    public List<ProductResponse> getProductByName(String productName) {

        log.info(productName+" Is found successfully");

        List<Product> products =  productRepository.findAllByProductNameIgnoreCase(productName);

        // If list is empty → return all products
        if (products.isEmpty()) {
            log.warn("No product found with name: " + productName);
            return productRepository.findAll()
                    .stream()
                    .map(ProductMapper::toDto)
                    .toList();
        }

        return products
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }

    @CacheEvict(value = "ProductResponse", key = "#id")
    public String  deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(("Product not found")));
       productRepository.delete(product);
       return "Product has been deleted successfully";
    }

    @CacheEvict(value = "ProductResponse", allEntries = true)
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {

        log.info("{} Is updated successfully", productRequest.getProductName());

        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(("Product id not available")));

        if (Objects.nonNull(product.getProductName())&&!"".equals(product.getProductName())) {
            product.setProductName(productRequest.getProductName());
        }
        if (Objects.nonNull(productRequest.getProductDescription())&&!"".equals(productRequest.getProductDescription())) {
            product.setProductDescription(productRequest.getProductDescription());
        }
        if (Objects.nonNull(productRequest.getProductPrice())&&!"".equals(productRequest.getProductPrice())) {
            product.setProductPrice(productRequest.getProductPrice());
        }
        if (Objects.nonNull(productRequest.getCategoryName())&&!"".equals(productRequest.getCategoryName())) {
            product.setCategoryName(productRequest.getCategoryName());
        }
        if (Objects.nonNull(productRequest.getBackgroundColor())&&!"".equals(productRequest.getBackgroundColor())) {
            product.setBackgroundColor(productRequest.getBackgroundColor());
        }
       var saveUpdatedProduct= productRepository.save(product);
        return ProductMapper.toDto(saveUpdatedProduct);

    }

    public List<ProductResponse> getAllProduct() {
       return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDto)
                .toList();
    }
}

