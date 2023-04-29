package com.labs.safertao.entity;

import java.util.List;

public record BulkCryptResponse(List<CryptResponse> responses, String minLengthString,
                                String maxLengthString, Integer avgLength) {}
