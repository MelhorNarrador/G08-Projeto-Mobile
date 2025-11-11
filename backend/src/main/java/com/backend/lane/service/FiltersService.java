package com.backend.lane.service;

import com.backend.lane.domain.Filters;

import java.util.List;

public interface FiltersService {

    List<Filters> getAllFilters();
    Filters createFilters (Filters filters);
    void deleteFilters (Integer id);
}
