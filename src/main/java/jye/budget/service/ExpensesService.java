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
    public Expenses check(Long expenseId, Long userId) {
        Expenses expenses = expensesMapper.findById(expenseId);
        if(expenses == null) {
            log.error("지출 정보 없음 : {}", expenseId);
            return null;
        }
        if(!expenses.getUserId().equals(userId)) {
            log.error("회원 ID 불일치 : expenses - {}, user - {}", expenses, userId);
            return null;
        }
        return expenses;
    }

    @Transactional
    public void delete(Long expenseId) {
        log.info("delete expenses : {}", expenseId);
        expensesMapper.delete(expenseId);
    }

    @Transactional
    public void update(Long expenseId, @Valid ExpensesForm expensesForm, Category category) {
        Expenses expenses = Expenses.builder()
                .expenseId(expenseId)
                .category(category)
                .expenseName(expensesForm.getExpenseName())
                .expenseDetail(expensesForm.getExpenseDetail())
                .amount(expensesForm.getAmount())
                .memo(expensesForm.getMemo())
                .relatedUrl(expensesForm.getRelatedUrl())
                .expenseDate(expensesForm.getExpenseDate())
                .isNecessary(expensesForm.isNecessary())
                .build();
        expensesMapper.update(expenses);
    }
}
