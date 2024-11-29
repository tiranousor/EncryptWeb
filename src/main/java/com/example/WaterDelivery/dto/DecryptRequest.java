package com.example.WaterDelivery.dto;

import lombok.Data;


@Data
public class DecryptRequest {
    private Long receiverId;
    private String method;
    private String encryptedMessage;

}
