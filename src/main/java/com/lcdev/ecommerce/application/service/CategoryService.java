package com.lcdev.ecommerce.application.service;

import com.lcdev.ecommerce.application.dto.category.CategoryTreeDTO;
import com.lcdev.ecommerce.domain.entities.Category;
import com.lcdev.ecommerce.infrastructure.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryTreeDTO> getCategoryTree() {
        List<Category> all = categoryRepository.findAll();

        Map<Long, CategoryTreeDTO> map = all.stream()
                .collect(Collectors.toMap(Category::getId, CategoryTreeDTO::fromEntity));

        List<CategoryTreeDTO> roots = new ArrayList<>();
        for (Category category : all) {
            CategoryTreeDTO dto = map.get(category.getId());
            if (category.getParentId() != null) {
                map.get(category.getParentId()).getChildren().add(dto);
            } else {
                roots.add(dto);
            }
        }
        return roots;
    }
}