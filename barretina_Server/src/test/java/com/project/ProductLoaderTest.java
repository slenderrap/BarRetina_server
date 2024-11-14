package com.project;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class ProductLoaderTest {
    /*
    @Test
    public void testLoadProducts() {
        ArrayList<Product> products = ProductLoader.loadProducts();
        assertNotNull(products);
        assertEquals(25, products.size());
        for (Product product : products) {
            assertNotNull(product.getId());
            assertNotNull(product.getName());
            assertNotEquals(0, product.getPrice());
            assertNotNull(product.getDescription());
            assertNotNull(product.getTags());
            assertNotNull(product.getImagePath());
            assertNotNull(product.getImageBase64(), "Image base64 is null");
            assertNotEquals(0, product.getImageBase64().length());
            assertTrue((product.getId() > 0 && product.getId() <= 25));
        }
    }

    @Test
    public void testGetTags() {
        ArrayList<String> tags = ProductLoader.getTags();
        assertNotNull(tags);
        assertEquals(7, tags.size(), "The number of tags is incorrect: " + tags);
    }*/
}
