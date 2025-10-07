package com.lcdev.ecommerce.application.dto.category;

import com.lcdev.ecommerce.domain.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeDTO {
    private Long id;
    private String name;
    private List<CategoryTreeDTO> children = new ArrayList<>();

    public static CategoryTreeDTO fromEntity(Category category) {
        return new CategoryTreeDTO(category.getId(), category.getName(), new ArrayList<>());
    }
}