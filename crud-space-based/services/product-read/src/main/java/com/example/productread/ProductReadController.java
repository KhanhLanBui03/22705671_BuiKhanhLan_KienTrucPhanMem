package com.example.productread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductReadController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping
    public List<Object> getAll() {
        Set<String> keys = redisTemplate.keys("PRODUCT:*");
        if (keys == null) return new ArrayList<>();
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable String id) {
        return (Product) redisTemplate.opsForValue().get("PRODUCT:" + id);
    }
}
