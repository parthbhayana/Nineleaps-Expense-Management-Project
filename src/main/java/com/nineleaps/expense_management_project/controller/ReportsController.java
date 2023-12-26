package com.nineleaps.expense_management_project.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import com.nineleaps.expense_management_project.dto.ReportsDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Hidden;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nineleaps.expense_management_project.entity.Reports;
import com.nineleaps.expense_management_project.service.IReportsService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@ApiResponses(
        value = {
                @ApiResponse(code = 200, message = "SUCCESS"),
                @ApiResponse(code = 401, message = "UNAUTHORIZED"),
                @ApiResponse(code = 403, message = "ACCESS_FORBIDDEN"),
                @ApiResponse(code = 404, message = "NOT_FOUND"),
                @ApiResponse(code = 500, message = "INTERNAL_SERVER_ERROR")
        })


public class ReportsController {

    @Autowired
    private IReportsService reportsService;

    @GetMapping("/getAllReports/{employeeId}")
    @ApiOperation(value = "Private Endpoint", tags = "private")
    public Set<Reports> getAllReports(@PathVariable Long employeeId) {
        return reportsService.getAllReports(employeeId);
    }

    @GetMapping("/getByReportId/{reportId}")
    public ResponseEntity<Reports> getReportByReportId(@PathVariable Long reportId) {
        try {
            Reports report = reportsService.getReportById(reportId);
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/getReportByEmployeeId/{employeeId}")
    public List<Reports> getReportByEmpId(@PathVariable Long employeeId, @RequestParam String request) {
        return reportsService.getReportByEmpId(employeeId, request);
    }

    @GetMapping("/getReportByEmpId/{employeeId}")
    public List<Reports> getReportByEmpId(
            @PathVariable Long employeeId,
            @RequestParam String request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Reports> reportsList = reportsService.getReportByEmpId(employeeId, request, page, size);
        return reportsList;
    }


    @GetMapping("/getReportsSubmittedToUser/{managerEmail}")
    public List<Reports> getReportsSubmittedToUser(@PathVariable String managerEmail, @RequestParam String request) {
        return reportsService.getReportsSubmittedToUser(managerEmail, request);
    }

    @GetMapping("/getAllSubmittedReports")
    public List<Reports> getAllSubmittedReports() {
        return reportsService.getAllSubmittedReports();
    }

    @GetMapping("/getAllReportsApprovedByManager")
    public List<Reports> getAllReportsApprovedByManager(@RequestParam String request) {
        return reportsService.getAllReportsApprovedByManager(request);
    }

    @PostMapping("/addReport/{employeeId}")
    public Reports addReport(@RequestBody ReportsDTO reportsDTO, @PathVariable Long employeeId,
                             @RequestParam List<Long> expenseIds) {
        return reportsService.addReport(reportsDTO, employeeId, expenseIds);
    }

    @PatchMapping("/addExpenseToReport/{reportId}")
    public Reports addExpensesToReport(@PathVariable Long reportId, @RequestParam List<Long> expenseIds) {
        return reportsService.addExpenseToReport(reportId, expenseIds);
    }

    @PostMapping("/submitReport/{reportId}")
    public void submitReport(@PathVariable Long reportId,
                             HttpServletResponse response) throws MessagingException, IOException {

        reportsService.submitReport(reportId, response);
    }

    @PostMapping("/submitReportToManager/{reportId}")
    public void submitReport(@PathVariable Long reportId, @RequestParam String managerEmail,
                             HttpServletResponse response) throws MessagingException, IOException {

        reportsService.submitReport(reportId, managerEmail, response);
    }

    @PatchMapping("/editReport/{reportId}")
    public List<Reports> editReport(@PathVariable Long reportId, @RequestParam String reportTitle,
                                    @RequestParam String reportDescription, @RequestParam List<Long> addExpenseIds,
                                    @RequestParam List<Long> removeExpenseIds) {
        return reportsService.editReport(reportId, reportTitle, reportDescription, addExpenseIds, removeExpenseIds);
    }



    @PostMapping("/rejectReportByFinance/{reportId}")
    public void rejectReportByFinance(@PathVariable Long reportId,
                                      @RequestParam(value = "comments", defaultValue = "null") String comments) {
        reportsService.rejectReportByFinance(reportId, comments);
    }

    @PostMapping("/hideReport/{reportId}")
    public void hideReport(@PathVariable Long reportId) {
        reportsService.hideReport(reportId);
    }

    @GetMapping("/getTotalAmountInrByReportId")
    public float totalAmountINR(@RequestParam Long reportId) {
        return reportsService.totalAmount(reportId);
    }

    @GetMapping("/getReportsInDateRange")
    public List<Reports> getReportsInDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, @RequestParam String request) {
        return reportsService.getReportsInDateRange(startDate, endDate, request);
    }

    @GetMapping("/getReportsSubmittedToUserInDateRange")
    public List<Reports> getReportsSubmittedToUserInDateRange(@RequestBody String managerEmail,
                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                              @RequestParam String request) {
        return reportsService.getReportsSubmittedToUserInDateRange(managerEmail, startDate, endDate, request);
    }

    @GetMapping("/getAmountOfReportsInDateRange")
    public String getAmountOfReportsInDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return reportsService.getAmountOfReportsInDateRange(startDate, endDate);
    }

    @GetMapping("/getTotalApprovedAmount")
    public float totalApprovedAmount(Long reportId) {
        return reportsService.totalApprovedAmount(reportId);
    }

    @PostMapping("/notifyHr/{reportId}")
    public void notifyHr(@RequestParam Long reportId) throws MessagingException {
        reportsService.notifyHR(reportId);
    }

    @PostMapping("/notifyLnD/{reportId}")
    public void notifyLnD(@RequestParam Long reportId) throws MessagingException {
        reportsService.notifyLnD(reportId);
    }

    @PostMapping("/updateExpenseStatus/{reportId}")
    public void updateExpenseStatus(@PathVariable Long reportId, @RequestParam String reviewTime,
                                    @RequestParam String json, @RequestParam String comments,
                                    HttpServletResponse response) throws ParseException, MessagingException,
            IOException {
        JSONParser parser = new JSONParser();

        Map<Long, Float> partialApprovedMap = new HashMap<>();
        List<Long> approvedIds = new ArrayList<>();
        List<Long> rejectedIds = new ArrayList<>();

        JSONArray jsonArray = (JSONArray) parser.parse(json);
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;

            long expenseId = (Long) jsonObject.get("expenseId");
            double amountApproved = (double) jsonObject.get("amountApproved");
            String status = (String) jsonObject.get("status");

            if (Objects.equals(status, "approved")) {
                approvedIds.add(expenseId);
            }
            if (Objects.equals(status, "rejected")) {
                rejectedIds.add(expenseId);
            }
            if (Objects.equals(status, "partiallyApproved")) {
                partialApprovedMap.put(expenseId, (float) amountApproved);
            }
        }
        reportsService.updateExpenseStatus(reportId, approvedIds, rejectedIds, partialApprovedMap, reviewTime,
                comments, response);
    }

}