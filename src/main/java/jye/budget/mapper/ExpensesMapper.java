package jye.budget.mapper;

import jye.budget.entity.Expenses;
import jye.budget.req.ExpensesReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExpensesMapper {
    List<Expenses> findByReqAndUserId(@Param("req") ExpensesReq req,
                                      @Param("userId") Long userId);

    void save(Expenses expenses);

    Expenses findById(@Param("expenseId") Long expenseId);

    void delete(@Param("expenseId") Long expenseId);

    void update(Expenses expenses);
}
