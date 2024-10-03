package jye.budget.entity;

import jye.budget.type.CalcType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetChange {
    private Long changeId;
    private Asset asset;
    private CalcType calcType;
    private int amount;
    private String changeDetail;
    private LocalDate changeDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
