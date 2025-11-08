package com.backend.lane.controllers;

import com.backend.lane.domain.Filters;
import com.backend.lane.service.FiltersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/filters")
public class FiltersController {
    FiltersService filtersService;

    public FiltersController(FiltersService filtersService){
        this.filtersService = filtersService;
    }

    @GetMapping("/get/filters")
    public List<Filters> getAllFilters() {
        return filtersService.getAllFilters();
    }

    @PostMapping("/create/filters")
    public Filters createFilters (@RequestBody Filters filters){
        return filtersService.createFilters(filters);
    }

    @DeleteMapping("/{id}")
    public void deleteFilters (@PathVariable Integer id){
        filtersService.deleteFilters(id);
    }
}
