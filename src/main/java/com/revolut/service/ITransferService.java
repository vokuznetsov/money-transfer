package com.revolut.service;

public interface ITransferService {

    void transfer(String sourceId, String destId, Long amount);

}
