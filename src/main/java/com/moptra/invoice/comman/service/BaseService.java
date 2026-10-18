package com.moptra.invoice.comman.service;

import java.util.List;

public interface BaseService {
    <REQ, RES> RES create(REQ request);

    <REQ, RES> RES update(Long id, REQ request);

    <RES> RES getById(Long id);

    <RES> List<RES> getAll();

    void delete(Long id);
}
