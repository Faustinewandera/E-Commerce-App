package Wandera.E_Commerce.App.Controllers;

import Wandera.E_Commerce.App.Dtos.ProductRequest;
import Wandera.E_Commerce.App.Dtos.ProductResponse;
import Wandera.E_Commerce.App.Services.ServiceImpl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductServiceImpl productService;

    @PostMapping("/addProduct")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse addProduct(@RequestBody ProductRequest productRequest) {
        try {
          return  productService.addProduct(productRequest);
        }catch (Exception ex){
            throw new RuntimeException("adding product failed try again");

        }

    }

    @GetMapping("/getAllProduct")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProduct(){
        return productService.getAllProduct();

    }
    @GetMapping("/name/{productName}")
    public List<ProductResponse> getProductByName(@PathVariable String productName) {
        return productService.getProductByName(productName);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @PutMapping("/updateProduct/{productId}")
    public ProductResponse updateProduct(@PathVariable Long productId,@RequestBody ProductRequest productRequest) {
        return productService.updateProduct(productId,productRequest);
    }

}


