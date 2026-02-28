package com.americobarber.service;

import com.americobarber.dto.request.CreateBarberRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.request.UserUpdateRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;

import java.util.List;

public interface AdminService {

    UserResponse createBarber(CreateBarberRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    List<UserResponse> listBarbers();

    List<UserResponse> listClients();

    ServiceResponse createService(ServiceRequest request);

    ServiceResponse updateService(Long id, ServiceRequest request);

    List<ServiceResponse> listAllServices();

    List<AppointmentResponse> listAllAppointments();
}
