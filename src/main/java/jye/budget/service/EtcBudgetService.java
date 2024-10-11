package jye.budget.service;

import jye.budget.entity.EtcBudget;
import jye.budget.mapper.EtcBudgetMapper;
import jye.budget.req.EtcBudgetReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtcBudgetService {

    private final EtcBudgetMapper etcBudgetMapper;

    @Transactional(readOnly = true)
    public List<EtcBudget> findByReqAndUserId(EtcBudgetReq req, Long userId) {
        return etcBudgetMapper.findByReqAndUserId(req, userId);
    }
}
