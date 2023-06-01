package com.nineleaps.expensemanagementproject.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

import java.util.List;

import javax.activation.DataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.nineleaps.expensemanagementproject.entity.Employee;
import com.nineleaps.expensemanagementproject.entity.Expense;
import com.nineleaps.expensemanagementproject.entity.FinanceApprovalStatus;
import com.nineleaps.expensemanagementproject.entity.Reports;
import com.nineleaps.expensemanagementproject.entity.StatusExcel;
import com.nineleaps.expensemanagementproject.repository.ReportsRepository;

@Service

public class ExcelGeneratorServiceAllSubmissionsImpl {

	@Autowired
	private ReportsRepository reportRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private IExpenseService expenseService;

	public boolean generateExcelAndSendEmail(HttpServletResponse response, LocalDate startDate, LocalDate endDate,
			StatusExcel status) throws Exception {

		ByteArrayOutputStream excelStream = new ByteArrayOutputStream();
		generateExcel(excelStream, startDate, endDate, status);
		byte[] excelBytes = excelStream.toByteArray();

		boolean emailsent=sendEmailWithAttachment("arjntmor9611@gmail.com", "BillFold:Excel Report", "Please find the attached Excel report.",
				excelBytes, "report.xls");
		return emailsent;
	}

	public void generateExcel(ByteArrayOutputStream excelStream, LocalDate startDate, LocalDate endDate,
			StatusExcel status) throws Exception {

		if (status == StatusExcel.ALL) {

			List<Reports> reportlist = reportRepo.findByDateSubmittedBetween(startDate, endDate);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Billfold_All_reports_All");
			HSSFRow row = sheet.createRow(0);

			row.createCell(0).setCellValue("Employee Email");
			row.createCell(1).setCellValue("Employee Name");
			// row.createCell(2).setCellValue("Calender Month");
			row.createCell(2).setCellValue("Report Id");
			row.createCell(3).setCellValue("Report Name");
			row.createCell(7).setCellValue("Submitted On");
			 row.createCell(8).setCellValue("Approved On");
			row.createCell(4).setCellValue("Approved by");
			row.createCell(5).setCellValue("Total Amount(INR)");
			row.createCell(6).setCellValue("Status");

			int dataRowIndex = 1;

			for (Reports reports : reportlist) {
				HSSFRow dataRow = sheet.createRow(dataRowIndex);
				
				Long id = reports.getReportId();
				List<Expense> expenseList = expenseService.getExpenseByReportId(id);

				if (!expenseList.isEmpty()) {
					Expense expense = expenseList.get(0);
					Employee employee = expense.getEmployee();
					dataRow.createCell(0).setCellValue(reports.getEmployeeMail());
					dataRow.createCell(1).setCellValue(employee.getFirstName() + " " + employee.getLastName());
					// dataRow.createCell(1).setCellValue(reports.getDateSubmitted().toString());
					dataRow.createCell(2).setCellValue(reports.getReportId());
					dataRow.createCell(3).setCellValue(reports.getReportTitle());
					dataRow.createCell(7).setCellValue(reports.getDateSubmitted().toString());
				 dataRow.createCell(8).setCellValue(reports.getManagerActionDate().toString());
					dataRow.createCell(4).setCellValue(reports.getManagerEmail());
					dataRow.createCell(5).setCellValue(reports.getTotalAmountINR());
					dataRow.createCell(6).setCellValue("Pending/Reimbursed");

					dataRowIndex++;
				}
			}
			// ServletOutputStream ops = response.getOutputStream();
			workbook.write(excelStream);
			workbook.close();
			// ops.close();

		}
		
		
		
		
		
		
		
		

		if (status == StatusExcel.PENDING) {
			List<Reports> reportlist = reportRepo.findByDateSubmittedBetween(startDate, endDate);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Billfold_All_reports_pending");
			HSSFRow row = sheet.createRow(0);

			row.createCell(0).setCellValue("Employee Email");
			row.createCell(1).setCellValue("Employee Name");
			// row.createCell(2).setCellValue("Calender Month");
			row.createCell(2).setCellValue("Report Id");
			row.createCell(3).setCellValue("Report Name");
			// row.createCell(5).setCellValue("Submitted On");
			// row.createCell(6).setCellValue("Approved On");
			row.createCell(4).setCellValue("Approved by");
			row.createCell(5).setCellValue("Total Amount(INR)");
			row.createCell(6).setCellValue("Status");

			int dataRowIndex = 1;

			for (Reports reports : reportlist) {
				if (reports.getFinanceapprovalstatus() == FinanceApprovalStatus.PENDING) {
					HSSFRow dataRow = sheet.createRow(dataRowIndex);
					dataRow.createCell(0).setCellValue(reports.getEmployeeMail());
					Long id = reports.getReportId();
					List<Expense> expenseList = expenseService.getExpenseByReportId(id);

					if (!expenseList.isEmpty()) {
						Expense expense = expenseList.get(0);
						Employee employee = expense.getEmployee();
						dataRow.createCell(1).setCellValue(employee.getFirstName() + " " + employee.getLastName());
					// dataRow.createCell(1).setCellValue(reports.getDateSubmitted().toString());
					dataRow.createCell(2).setCellValue(reports.getReportId());
					dataRow.createCell(3).setCellValue(reports.getReportTitle());
					// dataRow.createCell(1).setCellValue(reports.getDateSubmitted().toString());
					// dataRow.createCell(1).setCellValue(reports.getManagerActionDate().toString());
					dataRow.createCell(4).setCellValue(reports.getManagerEmail());
					dataRow.createCell(5).setCellValue(reports.getTotalAmountINR());
					dataRow.createCell(1).setCellValue("Pending");

					dataRowIndex++;
					}
				}
				// ServletOutputStream ops = response.getOutputStream();
				workbook.write(excelStream);
				workbook.close();
				// ops.close();

			}
		}
		
		
		
		
		
		
		
		
		

		if (status == StatusExcel.REIMBURSED) {
			List<Reports> reportlist = reportRepo.findByDateSubmittedBetween(startDate, endDate);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Billfold_All_reports_Reimbursed");
			HSSFRow row = sheet.createRow(0);

			row.createCell(0).setCellValue("Employee Email");
			row.createCell(1).setCellValue("Employee Name");
			// row.createCell(2).setCellValue("Calender Month");
			row.createCell(2).setCellValue("Report Id");
			row.createCell(3).setCellValue("Report Name");
			// row.createCell(5).setCellValue("Submitted On");
			// row.createCell(6).setCellValue("Approved On");
			row.createCell(4).setCellValue("Approved by");
			row.createCell(5).setCellValue("Total Amount(INR)");
			row.createCell(6).setCellValue("Status");

			int dataRowIndex = 1;

			for (Reports reports : reportlist) {
				if (reports.getFinanceapprovalstatus() == FinanceApprovalStatus.REIMBURSED) {
					HSSFRow dataRow = sheet.createRow(dataRowIndex);
					dataRow.createCell(0).setCellValue(reports.getEmployeeMail());
					Long id = reports.getReportId();
					List<Expense> expenseList = expenseService.getExpenseByReportId(id);

					if (!expenseList.isEmpty()) {
						Expense expense = expenseList.get(0);
						Employee employee = expense.getEmployee();
						dataRow.createCell(1).setCellValue(employee.getFirstName() + " " + employee.getLastName());
					dataRow.createCell(2).setCellValue(reports.getReportId());
					dataRow.createCell(3).setCellValue(reports.getReportTitle());
					// dataRow.createCell(1).setCellValue(reports.getDateSubmitted().toString());
					// dataRow.createCell(1).setCellValue(reports.getManagerActionDate().toString());
					dataRow.createCell(4).setCellValue(reports.getManagerEmail());
					dataRow.createCell(5).setCellValue(reports.getTotalAmountINR());
					dataRow.createCell(6).setCellValue("Reimbursed");

					dataRowIndex++;
				}
			}
			// ServletOutputStream ops = response.getOutputStream();
			workbook.write(excelStream);
			workbook.close();
//			ops.close();

		}
	}
	}

	private boolean sendEmailWithAttachment(String toEmail, String subject, String body, byte[] attachmentContent,
			String attachmentFilename) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body);

			DataSource attachment = new ByteArrayDataSource(attachmentContent, "application/vnd.ms-excel");
			helper.addAttachment(attachmentFilename, attachment);

			mailSender.send(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}
	}
}
