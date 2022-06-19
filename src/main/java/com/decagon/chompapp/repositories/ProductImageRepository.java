package com.decagon.chompapp.repositories;

import com.decagon.chompapp.models.Product;
import com.decagon.chompapp.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Transactional
    @Modifying
    @Query(value = "delete from ProductImage p where p.product.productId=:productId")
    void deleteByProduct(@Param("productId") long productId);
//    void deleteByProduct(Product product);
}
