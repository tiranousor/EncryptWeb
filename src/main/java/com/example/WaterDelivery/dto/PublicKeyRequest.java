package com.example.WaterDelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicKeyRequest {
    private Long userId;
    private Long contactId;
    private String method;
    private String publicKey;
}
