package ait.cohort34.forum.dto;

import lombok.Getter;

import java.time.LocalDate;
@Getter//нужен для получения JSON файла
public class DatePeriodDto {
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
