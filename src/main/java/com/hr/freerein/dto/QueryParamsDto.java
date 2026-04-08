package com.hr.freerein.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class QueryParamsDto {
    
    @Min(value = 0, message = "Page number cannot be negative")
    @Max(value = 1000, message = "Page number too large")
    private int page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private int size = 10;
    
    private String category;
    private String search;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
    
    // ✅ MANUAL CONSTRUCTORS
    public QueryParamsDto() {}
    
    public QueryParamsDto(int page, int size) {
        this.page = page;
        this.size = size;
    }
    
    // ✅ MANUAL GETTERS/SETTERS
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}