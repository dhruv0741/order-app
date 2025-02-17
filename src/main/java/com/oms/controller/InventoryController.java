package com.oms.controller;

import com.oms.pojo.SKU;
import com.oms.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/skus/{name}")
    public ResponseEntity<List<SKU>> getSKUsByName(@PathVariable String name) {
        return ResponseEntity.ok(inventoryService.findSKUsByName(name));
    }

    @PostMapping("/addStock")
    public ResponseEntity<String> addStock(@RequestParam Long skuId, @RequestParam int quantity) {
        inventoryService.addStock(skuId, quantity);
        return ResponseEntity.ok("Stock added successfully");
    }

    @PostMapping("/allocate")
    public ResponseEntity<String> allocateStock(@RequestParam Long skuId, @RequestParam Long orderNumber, @RequestParam int quantity) {
        boolean success = inventoryService.allocateStock(skuId, orderNumber, quantity);
        return success ? ResponseEntity.ok("Stock allocated successfully")
                       : ResponseEntity.badRequest().body("Insufficient stock");
    }

    @GetMapping("/audit")
    public ResponseEntity<String> auditStock() {
        boolean success = inventoryService.validateInventory();
        return success ? ResponseEntity.ok("Inventory is in sync")
                       : ResponseEntity.badRequest().body("Inventory is out of sync");
    }
}