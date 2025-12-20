package com.grocery.order.dto;

import com.grocery.order.OrderStatus;
import lombok.Getter;
import java.util.List;

@Getter
public class BulkStatusRequest {
    private List<Long> ids;
    private OrderStatus status;
}
