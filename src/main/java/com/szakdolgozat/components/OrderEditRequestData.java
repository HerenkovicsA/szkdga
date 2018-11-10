package com.szakdolgozat.components;

import java.util.Date;
import java.util.Set;

public class OrderEditRequestData {

	private Long orderId;
	private Long userId;
    private Date deadLine;
    private Double value;
    private Boolean done;
    private Set<String> removeProductFromOrder;
    private Integer quantities;
}
