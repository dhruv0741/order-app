package com.oms.service;

import com.oms.dao.OrderDao;
import com.oms.dao.SkuDao;
import com.oms.exception.SKUNotFoundException;
import com.oms.pojo.Order;
import com.oms.pojo.SKU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private SkuDao skuRepository;

    @Mock
    private OrderDao orderRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(skuRepository, orderRepository);
    }

    @Test
    void findSKUsByName_ShouldReturnMatchingSKUs() {
        // Arrange
        String searchName = "Test";
        List<SKU> expectedSkus = Arrays.asList(
            new SKU(1L, "Test SKU 1", 10, 10),
            new SKU(2L, "Test SKU 2", 20, 20)
        );
        when(skuRepository.findBySkuNameStartingWith(searchName)).thenReturn(expectedSkus);

        // Act
        List<SKU> result = inventoryService.findSKUsByName(searchName);

        // Assert
        assertEquals(expectedSkus, result);
        verify(skuRepository).findBySkuNameStartingWith(searchName);
    }

    @Test
    void addStock_ShouldIncreaseQuantity_WhenSKUExists() {
        // Arrange
        Long skuId = 1L;
        int addQuantity = 5;
        SKU existingSku = new SKU(skuId, "Test SKU", 10, 10);
        when(skuRepository.findById(skuId)).thenReturn(Optional.of(existingSku));

        // Act
        inventoryService.addStock(skuId, addQuantity);

        // Assert
        assertEquals(15, existingSku.getAvailableQuantity());
        assertEquals(15, existingSku.getTotalQuantity());
        verify(skuRepository).save(existingSku);
    }

    @Test
    void addStock_ShouldThrowException_WhenSKUNotFound() {
        // Arrange
        Long skuId = 1L;
        when(skuRepository.findById(skuId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SKUNotFoundException.class, () -> 
            inventoryService.addStock(skuId, 5)
        );
        verify(skuRepository, never()).save(any());
    }

    @Test
    void allocateStock_ShouldReturnTrue_WhenStockAvailable() {
        // Arrange
        Long skuId = 1L;
        Long orderNumber = 100L;
        int quantity = 1;
        SKU sku = new SKU(skuId, "Test SKU", 5, 5);
        when(skuRepository.findById(skuId)).thenReturn(Optional.of(sku));

        // Act
        boolean result = inventoryService.allocateStock(skuId, orderNumber, quantity);

        // Assert
        assertTrue(result);
        assertEquals(4, sku.getAvailableQuantity());
        assertEquals(5, sku.getTotalQuantity());
        verify(skuRepository).save(sku);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void allocateStock_ShouldReturnFalse_WhenNoStock() {
        // Arrange
        Long skuId = 1L;
        Long orderNumber = 100L;
        int quantity = 1;
        SKU sku = new SKU(skuId, "Test SKU", 0, 5);
        when(skuRepository.findById(skuId)).thenReturn(Optional.of(sku));

        // Act
        boolean result = inventoryService.allocateStock(skuId, orderNumber, quantity);

        // Assert
        assertFalse(result);
        assertEquals(0, sku.getAvailableQuantity());
        assertEquals(5, sku.getTotalQuantity());
        verify(skuRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void allocateStock_ShouldThrowException_WhenSKUNotFound() {
        // Arrange
        Long skuId = 1L;
        Long orderNumber = 100L;
        int quantity = 1;
        when(skuRepository.findById(skuId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SKUNotFoundException.class, () -> 
            inventoryService.allocateStock(skuId, orderNumber, quantity)
        );
        verify(skuRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void validateInventory_ShouldReturnTrue_WhenInventoryInSync() {
        // Arrange
        SKU sku1 = new SKU(1L, "SKU 1", 8, 10);
        SKU sku2 = new SKU(2L, "SKU 2", 15, 20);
        List<SKU> skus = Arrays.asList(sku1, sku2);
        
        List<Order> sku1Orders = Arrays.asList(new Order(1L, 1L, 2));
        List<Order> sku2Orders = Arrays.asList(new Order(2L, 2L, 5));
        
        when(skuRepository.findAll()).thenReturn(skus);
        when(orderRepository.findBySkuId(1L)).thenReturn(sku1Orders);
        when(orderRepository.findBySkuId(2L)).thenReturn(sku2Orders);

        // Act
        boolean result = inventoryService.validateInventory();

        // Assert
        assertTrue(result);
        verify(skuRepository).findAll();
        verify(orderRepository).findBySkuId(1L);
        verify(orderRepository).findBySkuId(2L);
    }

    @Test
    void validateInventory_ShouldReturnFalse_WhenInventoryOutOfSync() {
        // Arrange
        SKU sku = new SKU(1L, "SKU 1", 8, 10);
        List<SKU> skus = Arrays.asList(sku);
        
        List<Order> orders = Arrays.asList(new Order(1L, 1L, 3));
        
        when(skuRepository.findAll()).thenReturn(skus);
        when(orderRepository.findBySkuId(1L)).thenReturn(orders);

        // Act
        boolean result = inventoryService.validateInventory();

        // Assert
        assertFalse(result);
        verify(skuRepository).findAll();
        verify(orderRepository).findBySkuId(1L);
    }
}