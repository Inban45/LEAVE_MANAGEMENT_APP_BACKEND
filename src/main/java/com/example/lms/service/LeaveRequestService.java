package com.example.lms.service;

import com.example.lms.entity.LeaveRequest;
import com.example.lms.entity.Notification;
import com.example.lms.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final NotificationService notificationService;

    // Replace with actual manager ID retrieval logic
    private final Long MANAGER_ID = 1L;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                               NotificationService notificationService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.notificationService = notificationService;
    }

    // ✅ Overlap check added here
    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        // Check for overlapping leave
        List<LeaveRequest> overlappingLeaves = leaveRequestRepository.findOverlappingLeaves(
                leaveRequest.getEmployeeId(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        );

        if (!overlappingLeaves.isEmpty()) {
            throw new IllegalArgumentException("Overlapping leave request found! Please choose different dates.");
        }

        // Save leave request
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        // Notify manager
        Notification notification = new Notification(
                MANAGER_ID,
                "New leave request from employee ID: " + leaveRequest.getEmployeeId()
        );
        notificationService.createNotification(notification);

        return saved;
    }

    // Other existing methods...
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public Optional<LeaveRequest> getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id);
    }

    public LeaveRequest updateLeaveRequest(Long id, LeaveRequest leaveRequest) {
        return leaveRequestRepository.findById(id)
                .map(existing -> {
                    existing.setStartDate(leaveRequest.getStartDate());
                    existing.setEndDate(leaveRequest.getEndDate());
                    existing.setReason(leaveRequest.getReason());
                    existing.setStatus(leaveRequest.getStatus());
                    return leaveRequestRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Leave request not found"));
    }

    public void deleteLeaveRequest(Long id) {
        leaveRequestRepository.deleteById(id);
    }

    public List<LeaveRequest> getLeaveRequestsByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public List<LeaveRequest> getLeavesByUserId(Long userId) {
        return leaveRequestRepository.findByEmployeeId(userId);
    }

    public LeaveRequest saveLeave(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    public List<LeaveRequest> getAllLeaves() {
        return leaveRequestRepository.findAll();
    }
}
