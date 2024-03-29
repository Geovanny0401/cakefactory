package com.geovannycode.cakefactory.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItem {
    private final String title;
    private final BigDecimal value;
}
