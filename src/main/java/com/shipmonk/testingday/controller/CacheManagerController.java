package com.shipmonk.testingday.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// TODO: add security to this endpoint
@RestController
@RequestMapping(path = "/api/v1/admin/cache")
@RequiredArgsConstructor
public class CacheManagerController {

    private final CacheManager cacheManager;

    @RequestMapping(method = RequestMethod.POST, path = "/invalidate", produces = "application/json")
    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(c -> cacheManager.getCache(c).clear());
    }
}
