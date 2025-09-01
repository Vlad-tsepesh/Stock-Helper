package com.example.stockhelper.domain.model;

import org.springframework.core.io.Resource;

public record ImageRequest(String filename, Resource content) {}
