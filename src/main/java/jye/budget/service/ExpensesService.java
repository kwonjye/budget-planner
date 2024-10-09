package jye.budget.service;

import jye.budget.entity.Expenses;
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
}
