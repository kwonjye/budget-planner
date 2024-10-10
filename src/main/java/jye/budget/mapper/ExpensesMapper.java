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
}
