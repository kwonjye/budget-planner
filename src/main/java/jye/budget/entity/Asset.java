package jye.budget.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {
    private Long assetId;
    private Long userId;
    private String assetName;
    private int initialAmount;
    private int currentAmount;
    private boolean isAllocated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
