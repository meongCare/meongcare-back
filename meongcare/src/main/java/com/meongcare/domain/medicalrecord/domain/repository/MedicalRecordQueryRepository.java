package com.meongcare.domain.medicalrecord.domain.repository;

import com.meongcare.domain.medicalrecord.domain.entity.MedicalRecord;
import com.meongcare.domain.medicalrecord.domain.repository.vo.GetMedicalRecordsVo;
import com.meongcare.domain.medicalrecord.domain.repository.vo.QGetMedicalRecordsVo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.meongcare.domain.medicalrecord.domain.entity.QMedicalRecord.*;

@RequiredArgsConstructor
@Repository
public class MedicalRecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<MedicalRecord> getByIds(List<Long> medicalRecordIds) {
        return queryFactory
                .selectFrom(medicalRecord)
                .where(medicalRecord.id.in(medicalRecordIds))
                .fetch();
    }

    public void deleteByIds(List<Long> medicalRecordIds) {
        queryFactory
                .delete(medicalRecord)
                .where(medicalRecord.id.in(medicalRecordIds))
                .execute();
    }

    public List<GetMedicalRecordsVo> getByDate(LocalDateTime datetime) {
        return queryFactory
                .select(new QGetMedicalRecordsVo(
                        medicalRecord.id,
                        medicalRecord.dateTime
                ))
                .from(medicalRecord)
                .where(
                        dateEq(datetime))
                .orderBy(medicalRecord.dateTime.asc())
                .fetch();
    }

    private BooleanExpression dateEq(LocalDateTime datetime) {
        return medicalRecord.dateTime.between(
                LocalDateTime.of(datetime.getYear(), datetime.getMonth(), datetime.getDayOfMonth(), 0, 0, 0)
                ,LocalDateTime.of(datetime.getYear(), datetime.getMonth(), datetime.getDayOfMonth(), 23, 59, 59));
    }
}