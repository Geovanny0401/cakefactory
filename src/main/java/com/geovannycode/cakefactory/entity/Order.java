package com.geovannycode.cakefactory.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class Order {

    private final AddressPayment address;
    private final List<OrderItem> items;

    public String getTotal() {
        BigDecimal total = this.items.stream().map(OrderItem::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.setScale(2).toPlainString();
    }
}
