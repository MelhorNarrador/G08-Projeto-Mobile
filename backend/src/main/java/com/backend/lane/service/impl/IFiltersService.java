package com.backend.lane.service.impl;

import com.backend.lane.domain.Filters;
import com.backend.lane.repository.FiltersRepository;
import com.backend.lane.service.FiltersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IFiltersService implements FiltersService {

    private final FiltersRepository filtersRepository;

    @Autowired
    public IFiltersService(FiltersRepository filtersRepository) {
        this.filtersRepository = filtersRepository;
    }

    @Override
    public List<Filters> getAllFilters() {
        // ESTA LINHA É CRÍTICA: Vai buscar os dados à BD
        return filtersRepository.findAll();
    }

    @Override
    public Filters createFilters(Filters filters) {
        return filtersRepository.save(filters);
    }

    @Override
    public void deleteFilters(Integer id) {
        filtersRepository.deleteById(id);
    }
}