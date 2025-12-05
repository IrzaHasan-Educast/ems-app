package com.educast.ems.services;

import org.springframework.stereotype.Service;

import com.educast.ems.models.LeaveType;

@Service
public class LeaveTypeService {
    public LeaveType[] getAllLeaveTypes() {
    	System.out.println(LeaveType.values());
        return LeaveType.values();
    }
}