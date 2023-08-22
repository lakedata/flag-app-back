package com.flag.flag_back.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FixedFlagRes {

    private String name;
    private LocalDate date;
    private String startTime;
    private String endTime;
    private String place;
    private String memo;
    private List<String> members;

    public FixedFlagRes(String name, LocalDate date, String startTime, String endTime, String place, String memo, List<String> members) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.place = place;
        this.memo = memo;
        this.members = members;
    }
}
