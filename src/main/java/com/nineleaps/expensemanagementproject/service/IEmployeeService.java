package com.nineleaps.expensemanagementproject.service;

import java.util.List;

import com.nineleaps.expensemanagementproject.entity.Employee;



public interface IEmployeeService {

	public List<Employee> getAllEmployeeDetails();

	public Employee saveEmployeeDetails(Employee employeedetailsEntiry);

	public Employee getEmployeeDetailsById(Long empId);

	public void deleteEmployeeDetailsById(Long empId);

	public Employee updateEmployeeDetails(Employee employeeentity);
}
