package jye.budget.service;

import jakarta.validation.Valid;
import jye.budget.entity.Category;
import jye.budget.entity.Expenses;
import jye.budget.form.ExpensesForm;
import jye.budget.mapper.ExpensesMapper;
import jye.budget.req.ExpensesReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpensesService {

    private final ExpensesMapper expensesMapper;

    @Transactional(readOnly = true)
    public List<Expenses> findByReqAndUserId(ExpensesReq req, Long userId) {
        return expensesMapper.findByReqAndUserId(req, userId);
    }

    @Transactional
    public void save(Long userId, @Valid ExpensesForm expensesForm, Category category) {
        Expenses expenses = Expenses.builder()
                .userId(userId)
                .category(category)
                .expenseName(expensesForm.getExpenseName())
                .expenseDetail(expensesForm.getExpenseDetail())
                .amount(expensesForm.getAmount())
                .memo(expensesForm.getMemo())
                .relatedUrl(expensesForm.getRelatedUrl())
                .expenseDate(expensesForm.getExpenseDate())
                .isNecessary(expensesForm.isNecessary())
                .build();
        log.info("save expenses : {}", expenses);
        expensesMapper.save(expenses);
    }

    @Transactional(readOnly = true)
    public Expenses findById(Long expenseId) {
        Expenses expenses = expensesMapper.findById(expenseId);
        if(expenses == null) log.error("지출 정보 없음 : {}", expenseId);
        return expenses;
    }

    @Transactional
    public void delete(Long expenseId) {
        expensesMapper.delete(expenseId);
    }
}
