package com.grocery.settings;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class AdminSettings {

    @Id
    private Long id = 1L;
    private String storeName;
    private String storeAddress;
    private String storeContact;

    @ElementCollection
    private List<String> paymentMethods;
    @ElementCollection
    private List<String> deliveryOptions;
    private Double deliveryFee;
    private Boolean darkMode;
    private String primaryColor;
}
