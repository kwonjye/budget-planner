package jye.budget.mapper;

import jye.budget.entity.Category;
import jye.budget.entity.EtcBudget;
import jye.budget.req.EtcBudgetReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EtcBudgetMapper {
    List<EtcBudget> findByReqAndUserId(@Param("req") EtcBudgetReq req,
                                       @Param("userId") Long userId);

    List<Category> findCategoryBySearchDateAndUserId(@Param("searchDate") String searchDate,
                                                     @Param("userId") Long userId);

    void save(EtcBudget etcBudget);

    EtcBudget findById(@Param("etcBudgetId") Long etcBudgetId);

    void delete(@Param("etcBudgetId") Long etcBudgetId);

    void update(EtcBudget etcBudget);
}
