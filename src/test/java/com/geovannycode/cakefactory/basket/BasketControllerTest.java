package com.geovannycode.cakefactory.basket;

import com.geovannycode.cakefactory.controller.BasketController;
import com.geovannycode.cakefactory.entity.Address;
import com.geovannycode.cakefactory.entity.BasketItem;
import com.geovannycode.cakefactory.entity.Item;
import com.geovannycode.cakefactory.repository.Basket;
import com.geovannycode.cakefactory.client.BrowserClient;
import com.geovannycode.cakefactory.service.AccountService;
import com.geovannycode.cakefactory.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = BasketController.class)
@Import(OAuth2ClientAutoConfiguration.class)
public class BasketControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    Basket basket;

    @MockBean
    AddressService addressService;

    @MockBean
    AccountService accountService;

    @Test
    void addsItemsToBasket() throws Exception {
        String expectedSku = "rv";

        mockMvc.perform(MockMvcRequestBuilders.post("/basket").param("sku", expectedSku).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        verify(basket).add(expectedSku);
    }

    @Test
    void removesItemsFromBasket() throws Exception {
        String expectedSku = "rv";

        mockMvc.perform(MockMvcRequestBuilders.post("/basket/delete").param("sku", expectedSku).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/basket"));

        verify(basket).remove(expectedSku);
    }

    @Test
    void showsBasket() {
        BasketItem basketItem1 = new BasketItem(new Item("test1", "Test 1", BigDecimal.valueOf(3)), 2);
        BasketItem basketItem2 = new BasketItem(new Item("test2", "Test 2", BigDecimal.valueOf(5)), 1);
        when(basket.getItems()).thenReturn(Arrays.asList(basketItem1, basketItem2));

        BrowserClient client = new BrowserClient(mockMvc);
        client.goToBasket();

        assertThat(client.getBasketItemQtyLabel("Test 1")).isEqualTo("2");
        assertThat(client.getBasketItemQtyLabel("Test 2")).isEqualTo("1");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void prePopulatesBasketFields() {
        String expectedAddressLine1 = "address line 1";
        String expectedAddressLine2 = "address line 2";
        String expectedPostcode = "postcode";

        Address account = new Address(expectedAddressLine1, expectedAddressLine2, expectedPostcode);
        when(addressService.findOrEmpty("test@example.com")).thenReturn(account);

        BrowserClient browserClient = new BrowserClient(mockMvc);
        browserClient.goToBasket();

        assertThat(browserClient.getAddressLine1()).isEqualTo(expectedAddressLine1);
        assertThat(browserClient.getAddressLine2()).isEqualTo(expectedAddressLine2);
        assertThat(browserClient.getPostcode()).isEqualTo(expectedPostcode);
    }

}
