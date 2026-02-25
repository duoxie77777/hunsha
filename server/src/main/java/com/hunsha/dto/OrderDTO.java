package com.hunsha.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderDTO {

    private Long packageId;

    private LocalDate shootDate;

    private String shootTime;

    private String contactName;

    private String contactPhone;

    private String remark;
}
