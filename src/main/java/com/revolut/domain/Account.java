package com.revolut.domain;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    private String id;
    private String name;
    private Long balance;

    public Account(String name, Long balance) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.balance = balance;
    }
}
