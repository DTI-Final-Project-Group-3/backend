package com.warehub.warehub.infrastructure.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {
    private String orderId;
    private String grossAmount;

//    private TransactionDetails transactionDetails;
//    private CreditCard creditCard;
//    private List<ItemDetails> itemDetails;
//    private CustomerDetails customerDetails;
//    private ShippingAddress shippingAddress;
}

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class TransactionDetails {
//    private String orderId;
//    private String transactionId;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class CreditCard {
//    private Boolean secure;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class ItemDetails {
//    private String itemId;
//    private BigDecimal price;
//    private Integer quantity;
//    private String itemName;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class CustomerDetails {
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String phone;
//}
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//class ShippingAddress {
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String phone;
//    private String address;
//    private String city;
//    private String postalCode;
//    private String countryCode;
//}