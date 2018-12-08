package com.revolut.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект представляет описание ошибки в API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private int code;
    private String message;

}
