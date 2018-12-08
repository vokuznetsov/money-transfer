package com.revolut.domain;

import lombok.Data;

@Data
public class Transfer {

    private Long fromId;
    private Long toId;
    private Long amount;

}
