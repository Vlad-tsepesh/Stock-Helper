package com.stockhelper.domain.model;

import java.util.List;

public record ImageDescription(
    String title,
    String description,
    List<String> keywords
) {}
