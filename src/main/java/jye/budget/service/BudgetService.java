package jye.budget.service;

import jakarta.validation.Valid;
import jye.budget.entity.*;
import jye.budget.form.AssetChangeForm;
import jye.budget.mapper.AssetMapper;
import jye.budget.mapper.BudgetMapper;
import jye.budget.mapper.CategoryMapper;
import jye.budget.dto.BudgetDto;
import jye.budget.type.CalcType;
import jye.budget.type.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetMapper budgetMapper;
    private final AssetMapper assetMapper;
    private final CategoryMapper categoryMapper;
    private final AssetService assetService;

    @Transactional(readOnly = true)
    public BudgetDto findByYearMonthAndUserId(String yearMonth, Long userId, boolean forUpdate) {
        Budget budget = budgetMapper.findByYearMonth(yearMonth, userId);
        log.info("find budget : {}", budget);

        if(budget != null) {
            return getBudgetDtoByBudget(budget, forUpdate);
        } else {
            List<Asset> assets = assetMapper.findByUserIdAndAllocated(userId);
            List<Category> FixedExpansesCategories = categoryMapper.findByUserIdAndType(userId, CategoryType.FIXED_EXPENSE);

            log.info("assets : {}", assets);
            log.info("FixedExpansesCategories : {}", FixedExpansesCategories);

            List<BudgetAllocation> budgetAllocations = assets.stream().map(asset -> BudgetAllocation.builder().asset(asset).build()).toList();
            List<FixedExpenses> fixedExpenses = FixedExpansesCategories.stream().map(fixedExpensesCategory -> FixedExpenses.builder().category(fixedExpensesCategory).build()).toList();

            return BudgetDto.builder()
                    .budget(Budget.builder().yearMonth(yearMonth).build())
                    .budgetAllocations(budgetAllocations)
                    .fixedExpenses(fixedExpenses)
                    .build();
        }
    }

    @Transactional
    public void save(@Valid BudgetDto req, Long userId) {
        log.info("save budget : {}", req.getBudget());
        budgetMapper.save(req.getBudget());

        log.info("save budgetAllocation : {}", req.getBudgetAllocations());
        if(req.getBudgetAllocations() != null) {
            req.getBudgetAllocations().forEach(budgetAllocation -> saveBudgetAllocation(req, userId, budgetAllocation));
        }

        log.info("save fixedExpenses : {}", req.getFixedExpenses());
        if(req.getFixedExpenses() != null) {
            req.getFixedExpenses().forEach(fixedExpenses -> {
                if(fixedExpenses.getAmount() > 0) {
                    fixedExpenses.setBudgetId(req.getBudget().getBudgetId());
                    budgetMapper.saveFixedExpenses(fixedExpenses);
                }
            });
        }
    }

    private void saveBudgetAllocation(BudgetDto req, Long userId, BudgetAllocation budgetAllocation) {
        if(budgetAllocation.getAmount() > 0) {
            Asset asset = assetService.check(budgetAllocation.getAsset().getAssetId(), userId);
            AssetChangeForm assetChangeForm = AssetChangeForm.builder()
                    .assetId(asset.getAssetId())
                    .calcType(CalcType.ADD)
                    .amount(budgetAllocation.getAmount())
                    .changeDetail(req.getBudget().getYearMonth() + " 예산 배분")
                    .changeDate(LocalDate.now())
                    .build();
            AssetChange assetChange = assetService.change(assetChangeForm, asset);
            budgetAllocation.setBudgetId(req.getBudget().getBudgetId());
            budgetAllocation.setChangeId(assetChange.getChangeId());
            budgetMapper.saveBudgetAllocation(budgetAllocation);
        }
    }

    @Transactional
    public void update(@Valid BudgetDto req, Long userId) {
        log.info("update budget : {}", req.getBudget());
        budgetMapper.update(req.getBudget());

        log.info("update budgetAllocation : {}", req.getBudgetAllocations());
        if(req.getBudgetAllocations() != null) {
            req.getBudgetAllocations().forEach(budgetAllocation -> {
                if(budgetAllocation.getBudgetAllocationId() != null) {
                    Long changeId = budgetMapper.findChangeIdByBudgetAllocationId(budgetAllocation.getBudgetAllocationId());
                    Asset asset = assetService.check(budgetAllocation.getAsset().getAssetId(), userId);
                    AssetChangeForm assetChangeForm = AssetChangeForm.builder()
                            .assetId(asset.getAssetId())
                            .calcType(CalcType.ADD)
                            .amount(budgetAllocation.getAmount())
                            .changeDetail(req.getBudget().getYearMonth() + " 예산 배분")
                            .changeDate(LocalDate.now())
                            .build();
                    assetService.updateChange(changeId, assetChangeForm, asset);
                    budgetAllocation.setBudgetId(req.getBudget().getBudgetId());
                    budgetAllocation.setChangeId(changeId);
                    budgetMapper.updateBudgetAllocation(budgetAllocation);
                } else {
                    saveBudgetAllocation(req, userId, budgetAllocation);
                }
            });
        }

        log.info("update fixedExpenses : {}", req.getFixedExpenses());
        if(req.getFixedExpenses() != null) {
            req.getFixedExpenses().forEach(fixedExpenses -> {
                if(fixedExpenses.getFixedExpenseId() != null) {
                    budgetMapper.updateFixedExpenses(fixedExpenses);
                } else {
                    if(fixedExpenses.getAmount() > 0) {
                        fixedExpenses.setBudgetId(req.getBudget().getBudgetId());
                        budgetMapper.saveFixedExpenses(fixedExpenses);
                    }
                }
            });
        }
    }

    @Transactional(readOnly = true)
    public BudgetDto getRecentBudget(Long userId) {
        Budget budget = budgetMapper.findRecent(userId);
        log.info("get recent budget : {}", budget);

        if(budget != null) {
            return getBudgetDtoByBudget(budget, true);
        }
        return null;
    }

    private BudgetDto getBudgetDtoByBudget(Budget budget, boolean forUpdate) {
        List<BudgetAllocation> budgetAllocations;
        List<FixedExpenses> fixedExpenses;

        if(forUpdate) {
            budgetAllocations = budgetMapper.findBudgetAllocationByBudgetIdForUpdate(budget.getBudgetId(), budget.getUserId());
            fixedExpenses = budgetMapper.findFixedExpensesByBudgetIdForUpdate(budget.getBudgetId(), budget.getUserId());
        } else {
            budgetAllocations = budgetMapper.findBudgetAllocationByBudgetId(budget.getBudgetId());
            fixedExpenses = budgetMapper.findFixedExpensesByBudgetId(budget.getBudgetId());
        }

        log.info("budgetAllocations : {}", budgetAllocations);
        log.info("fixedExpenses : {}", fixedExpenses);

        return BudgetDto.builder()
                .budget(budget)
                .budgetAllocations(budgetAllocations)
                .fixedExpenses(fixedExpenses)
                .build();
    }

    @Transactional(readOnly = true)
    public Budget check(Long budgetId, Long userId) {
        Budget budget = findById(budgetId);
        if(budget == null) {
            log.info("예산 정보 없음 : {}", budgetId);
            return null;
        }
        if(!budget.getUserId().equals(userId)) {
            log.error("회원 ID 불일치 : budget - {}, userId - {}", budget, userId);
            return null;
        }
        return budget;
    }

    @Transactional(readOnly = true)
    public Budget findById(Long budgetId) {
        return budgetMapper.findById(budgetId);
    }

    @Transactional
    public void delete(Long budgetId) {
        log.info("delete budget : {}", budgetId);
        List<BudgetAllocation> budgetAllocations = budgetMapper.findBudgetAllocationByBudgetId(budgetId);
        budgetAllocations.forEach(budgetAllocation -> {
            AssetChange assetChange = assetService.findChangeById(budgetAllocation.getChangeId());
            assetService.deleteChange(assetChange.getChangeId(), assetChange.getAsset());
        });
        budgetMapper.delete(budgetId);
    }
}
