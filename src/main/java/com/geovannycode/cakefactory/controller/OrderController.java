package com.geovannycode.cakefactory.controller;

import com.geovannycode.cakefactory.repository.Basket;
import com.geovannycode.cakefactory.service.OrderReceivedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final Basket basket;
    private final ApplicationEventPublisher eventPublisher;

    public OrderController(Basket basket, ApplicationEventPublisher eventPublisher) {
        this.basket = basket;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    ModelAndView order() {
        return new ModelAndView("order", Map.of("basketTotal", 0));
    }

    @PostMapping
    String completeOrder(@RequestParam String addressLine1, @RequestParam String addressLine2, @RequestParam String postcode) {
        final String address = Stream.of(addressLine1, addressLine2, postcode).collect(Collectors.joining(", "));
        this.eventPublisher.publishEvent(new OrderReceivedEvent(address, basket.getItems()));
        this.basket.clear();
        return "redirect:/order";
    }
}
