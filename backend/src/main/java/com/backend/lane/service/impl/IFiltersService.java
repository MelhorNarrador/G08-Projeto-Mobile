package com.backend.lane.service.impl;

import com.backend.lane.domain.Filters;
import com.backend.lane.service.FiltersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IFiltersService implements FiltersService {
    @Override
    public List<Filters> getAllFilters() {
        return List.of();
    }

    @Override
    public Filters createFilters(Filters filters) {
        return null;
    }

    @Override
    public void deleteFilters(Integer id) {

    }
}
