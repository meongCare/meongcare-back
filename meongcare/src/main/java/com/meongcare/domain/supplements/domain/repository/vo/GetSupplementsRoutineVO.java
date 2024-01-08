package com.meongcare.domain.supplements.domain.repository.vo;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class GetSupplementsRoutineVO {
    private Long supplementsRecordId;
    private Long supplementsId;
    private String name;
    private int intakeCount;
    private String intakeUnit;
    private LocalTime intakeTime;
    private boolean intakeStatus;

    @QueryProjection
    public GetSupplementsRoutineVO(Long supplementsRecordId, Long supplementsId, String name, int intakeCount, String intakeUnit, LocalTime intakeTime, boolean intakeStatus) {
        this.supplementsRecordId = supplementsRecordId;
        this.supplementsId = supplementsId;
        this.name = name;
        this.intakeCount = intakeCount;
        this.intakeUnit = intakeUnit;
        this.intakeTime = intakeTime;
        this.intakeStatus = intakeStatus;
    }
}
