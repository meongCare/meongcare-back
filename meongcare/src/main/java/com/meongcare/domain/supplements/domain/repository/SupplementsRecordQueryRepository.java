package com.meongcare.domain.supplements.domain.repository;

import com.meongcare.domain.dog.domain.entity.QDog;
import com.meongcare.domain.member.domain.entity.QMember;
import com.meongcare.domain.supplements.domain.entity.QSupplements;
import com.meongcare.domain.supplements.domain.repository.vo.GetAlarmSupplementsVO;
import com.meongcare.domain.supplements.domain.repository.vo.GetSupplementsRoutineVO;
import com.meongcare.domain.supplements.domain.repository.vo.QGetAlarmSupplementsVO;
import com.meongcare.domain.supplements.domain.repository.vo.QGetSupplementsRoutineVO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.meongcare.domain.dog.domain.entity.QDog.*;
import static com.meongcare.domain.member.domain.entity.QMember.*;
import static com.meongcare.domain.supplements.domain.entity.QSupplements.*;
import static com.meongcare.domain.supplements.domain.entity.QSupplementsRecord.*;

@RequiredArgsConstructor
@Repository
public class SupplementsRecordQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<GetSupplementsRoutineVO> getByDogIdAndDate(Long dogId, LocalDate date) {
        return queryFactory
                .select(new QGetSupplementsRoutineVO(
                        supplementsRecord.id,
                        supplementsRecord.supplements.id,
                        supplementsRecord.supplements.name,
                        supplementsRecord.supplementsTime.intakeCount,
                        supplementsRecord.supplements.intakeUnit,
                        supplementsRecord.supplementsTime.intakeTime,
                        supplementsRecord.intakeStatus
                ))
                .from(supplementsRecord)
                .where(
                        dogIdEq(dogId), dateEq(date), isActive(), isNotDeleted()
                )
                .fetch();
    }

    public int getTotalRecordCount(Long dogId, LocalDate date) {
        return queryFactory
                .select(supplementsRecord.count())
                .from(supplementsRecord)
                .where(dogIdEq(dogId), dateEq(date), isNotDeleted())
                .fetchOne().intValue();
    }

    public int getIntakeRecordCount(Long dogId, LocalDate date) {
        return queryFactory
                .select(supplementsRecord.count())
                .from(supplementsRecord)
                .where(
                        dogIdEq(dogId), dateEq(date), isIntakeStatus(), isNotDeleted())
                .fetchOne().intValue();
    }

    public List<GetAlarmSupplementsVO> findAllAlarmSupplementsByTime(LocalTime time, LocalTime fiftyNineSecondsLater) {
        return queryFactory
                .select(new QGetAlarmSupplementsVO(
                        member.fcmToken,
                        dog.name,
                        supplements.name))
                .from(supplementsRecord)
                .innerJoin(supplementsRecord.supplements, supplements)
                .innerJoin(supplements.dog, dog)
                .innerJoin(dog.member, member)
                .where(isActive(), isNotDeleted(), isNotIntakeStatus(), isTimeEQ(time, fiftyNineSecondsLater))
                .fetch();
    }

    private BooleanExpression dogIdEq(Long dogId) {
        return supplementsRecord.supplements.dog.id.eq(dogId);
    }

    private BooleanExpression dateEq(LocalDate date) {
        return supplementsRecord.date.eq(date);
    }

    private BooleanExpression isActive() {
        return supplementsRecord.supplements.isActive.isTrue();
    }

    private BooleanExpression isIntakeStatus() { return supplementsRecord.intakeStatus.isTrue();}

    private BooleanExpression isNotIntakeStatus() { return supplementsRecord.intakeStatus.isFalse();}

    private BooleanExpression isNotDeleted() { return supplementsRecord.supplements.deleted.isFalse();}

    private BooleanExpression isTimeEQ(LocalTime time, LocalTime fiftyNineSecondsLater) {
        return supplementsRecord.supplementsTime.intakeTime.between(time, fiftyNineSecondsLater);
    }

}
