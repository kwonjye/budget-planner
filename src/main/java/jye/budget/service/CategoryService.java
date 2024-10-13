package jye.budget.service;

import jakarta.validation.Valid;
import jye.budget.entity.Category;
import jye.budget.form.CategoryForm;
import jye.budget.mapper.CategoryMapper;
import jye.budget.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public Category check(Long categoryId, Long userId) {
        Category category = categoryMapper.findById(categoryId);
        if(category == null) {
            log.error("카테고리 정보 없음 : {}", categoryId);
            return null;
        }
        if(!category.getUserId().equals(userId)) {
            log.error("회원 ID 불일치 : category - {}, user - {}", category, userId);
            return null;
        }
        return category;
    }

    @Transactional(readOnly = true)
    public List<Category> findByUserIdAndType(Long userId, CategoryType categoryType) {
        return categoryMapper.findByUserIdAndType(userId, categoryType);
    }

    @Transactional
    public void delete(Long categoryId) {
        log.info("delete category - {}", categoryId);
        categoryMapper.delete(categoryId);
    }

    @Transactional
    public void update(Long categoryId, @Valid CategoryForm categoryForm) {
        Category category = Category.builder()
                .categoryId(categoryId)
                .categoryName(categoryForm.getCategoryName())
                .categoryColor(categoryForm.getCategoryColor())
                .build();
        categoryMapper.update(category);
    }
}
