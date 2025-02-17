package com.oms.service;

import com.oms.pojo.Order;
import com.oms.pojo.SKU;
import com.oms.dao.OrderDao;
import com.oms.dao.SkuDao;
import com.oms.exception.SKUNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final SkuDao skuRepository;
    private final OrderDao orderRepository;

    public InventoryService(SkuDao skuRepository, OrderDao orderRepository) {
        this.skuRepository = skuRepository;
        this.orderRepository = orderRepository;
    }

    public List<SKU> findSKUsByName(String name) {
        return skuRepository.findBySkuNameStartingWith(name);
    }

    public void addStock(Long skuId, int quantity) {
        SKU sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new SKUNotFoundException(skuId));
        sku.setAvailableQuantity(sku.getAvailableQuantity() + quantity);
        sku.setTotalQuantity(sku.getTotalQuantity() + quantity);
        skuRepository.save(sku);
    }

    public boolean allocateStock(Long skuId, Long orderNumber, int quantity) {
        SKU sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new SKUNotFoundException(skuId));
        
        if (sku.getAvailableQuantity() >= quantity) {
            sku.setAvailableQuantity(sku.getAvailableQuantity() - quantity);
            skuRepository.save(sku);

            Order order = new Order(orderNumber, skuId, quantity);
            orderRepository.save(order);
            return true;
        }
        return false;
    }

    public boolean validateInventory() {
        List<SKU> allSkus = skuRepository.findAll();
        
        for (SKU sku : allSkus) {
            int orderedQuantity = orderRepository.findBySkuId(sku.getSkuId())
                    .stream()
                    .mapToInt(Order::getQuantity)
                    .sum();
                    
            if (sku.getTotalQuantity() != (sku.getAvailableQuantity() + orderedQuantity)) {
                return false;
            }
        }
        return true;
    }
}