package com.grocery.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.grocery.auth.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiverName; // Kiske naam par order aayega
    private String mobileNumber;
    private String flatHouseNo;
    private String area;
    private String city;
    private String state;
    private String zipCode;
    private String addressType; // e.g., "Home", "Office", "Other"
    private boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}