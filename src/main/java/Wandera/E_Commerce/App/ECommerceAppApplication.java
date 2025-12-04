package Wandera.E_Commerce.App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ECommerceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommerceAppApplication.class, args);
	}
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "ProductResponse",
                "CategoryResponse",
                "ProductResponse",
                "ProfileResponse",
                "LoginResponse",
                "default"
        );
    }

}
