package com.project.back_end.DTO;

import java.util.List;

import com.project.back_end.Entity.ClinicLocation;
import com.project.back_end.Entity.Doctor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DoctorAvailabilityDTO {
    private Long                id;
    private String              fullName;
    private String              specialty;
    private ClinicLocation      clinicLocation;
    private List<String>        availableTimes;   // 当日の残枠だけ

    public DoctorAvailabilityDTO(Doctor d, List<String> remains) {
        this.id              = d.getId();
        this.fullName        = d.getUser().getFullName();
        this.specialty       = d.getSpecialty();
        this.clinicLocation  = d.getClinicLocation();
        this.availableTimes  = remains;
    }
}

