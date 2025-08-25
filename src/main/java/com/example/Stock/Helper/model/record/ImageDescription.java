package com.example.Stock.Helper.model.record;

import java.util.List;

public record ImageDescription(
    String title,
    String description,
    List<String> keywords
) {}
