package com.nineleaps.expensemanagementproject.service;

import com.nineleaps.expensemanagementproject.constants.Constants;
import com.nineleaps.expensemanagementproject.repository.EmployeeRepository;
import com.nineleaps.expensemanagementproject.repository.ExpenseRepository;
import com.nineleaps.expensemanagementproject.repository.ReportsRepository;
import org.springframework.stereotype.Service;
import com.nineleaps.expensemanagementproject.entity.Employee;
import com.nineleaps.expensemanagementproject.entity.Expense;
import com.nineleaps.expensemanagementproject.entity.Reports;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


@Service
public class EmailServiceImpl implements IEmailService {

    @Autowired
    private IReportsService reportsService;
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private IExpenseService expenseService;
    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private ReportsRepository reportsRepository;
    @Autowired
    private IPdfGeneratorService pdfGeneratorService;

    private final JavaMailSender javaMailSender;


    public EmailServiceImpl(JavaMailSender mailSender) {
        this.javaMailSender = mailSender;
    }


    @Override
    public void notifyHr(Long reportId, String hrEmail, String hrName) throws MessagingException {
        Reports report = reportsService.getReportById(reportId);
        if (reportId != null) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper eMail = new MimeMessageHelper(message, true);
            eMail.setFrom(Constants.CONSTANT9);
            eMail.setTo(hrEmail);
            eMail.setSubject("Expense Report: " + report.getReportTitle());
            eMail.setText(Constants.CONSTANT12 + hrName + Constants.CONSTANT9
                    + "CONTENT HERE!!"
                    + Constants.CONSTANT9 + Constants.CONSTANT9);
            javaMailSender.send(message);
        }
    }

    @Override
    public void notifyLnD(Long reportId, String lndEmail, String lndName) throws MessagingException {
        Reports report = reportsService.getReportById(reportId);
        if (reportId != null) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper eMail = new MimeMessageHelper(message, true);
            eMail.setFrom(Constants.CONSTANT9);
            eMail.setTo(lndEmail);
            eMail.setSubject("Expense Report: " + report.getReportTitle()); //
            eMail.setText(Constants.CONSTANT12 + lndName + Constants.CONSTANT9
                    + "CONTENT HERE!!"
                    + Constants.CONSTANT9 + Constants.CONSTANT9);
            javaMailSender.send(message);
        }
    }

    @Override
    public void managerNotification(Long reportId, List<Long> expenseIds, HttpServletResponse response)
            throws IOException, MessagingException {
        Reports report = reportsService.getReportById(reportId);
        List<Expense> expenseList = expenseService.getExpenseByReportId(reportId);
        if (!expenseList.isEmpty()) {
            Expense expense = expenseList.get(0);
            Employee employee = expense.getEmployee();
            if (employee != null && employee.getManagerEmail() != null) {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper eMail = new MimeMessageHelper(message, true);
                eMail.setFrom(Constants.CONSTANT9);
                eMail.setTo(employee.getManagerEmail());
                eMail.setSubject("BillFold - " + employee.getFirstName() + " " + employee.getLastName());
                eMail.setText(Constants.CONSTANT12 + employee.getManagerName() + ".\n\n"
                        + employee.getFirstName() + " " + employee.getLastName()
                        + " has submitted you a report for approval. As a designated manager, we kindly request your prompt attention to review and take necessary action on the report."
                        + "\n\nBelow are the details of the report submission:" + Constants.CONSTANT6 + report.getReportTitle()
                        + "\nSubmitter's Name: " + employee.getFirstName() + " " + employee.getLastName()
                        + "\nSubmission Date: " + report.getDateSubmitted() + Constants.CONSTANT9
                        + report.getTotalAmount()
                        + "\n\nPlease log in to your Billfold account to access the report and review its contents. We kindly request you to carefully evaluate the report and take appropriate action based on your assessment."
                        + Constants.CONSTANT9 + Constants.CONSTANT9);
                byte[] fileData = pdfGeneratorService.export(reportId, expenseIds, response,"Manager");
                ByteArrayResource resource = new ByteArrayResource(fileData);
                eMail.addAttachment(Constants.CONSTANT6, resource);
                javaMailSender.send(message);
            } else {
                throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
            }
        } else {
            throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
        }
    }

    @Override
    public void managerNotification(Long reportId, List<Long> expenseIds, String managerEmail,
                                    HttpServletResponse response) throws IOException, MessagingException {

        Reports report = reportsService.getReportById(reportId);
        List<Expense> expenseList = expenseService.getExpenseByReportId(reportId);
        if (!expenseList.isEmpty()) {
            Expense expense = expenseList.get(0);
            Employee employee = expense.getEmployee();
            if (employee != null && employee.getManagerEmail() != null) {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper eMail = new MimeMessageHelper(message, true);
                eMail.setFrom(Constants.CONSTANT9);
                eMail.setTo(managerEmail);
                eMail.setSubject("BillFold - " + employee.getFirstName() + " " + employee.getLastName());
                eMail.setText(Constants.CONSTANT12 + employee.getManagerName() + Constants.CONSTANT9
                        + employee.getFirstName() + " " + employee.getLastName()
                        + " has submitted you a report for approval. As a designated manager, we kindly request your prompt attention to review and take necessary action on the report."
                        + "\n\nBelow are the details of the report submission:" + Constants.CONSTANT6 + report.getReportTitle()
                        + "\nSubmitter's Name: " + employee.getFirstName() + " " + employee.getLastName()
                        + "\nSubmission Date: " + report.getDateSubmitted() + Constants.CONSTANT9
                        + report.getTotalAmount()
                        + "\n\nPlease log in to your Billfold account to access the report and review its contents. We kindly request you to carefully evaluate the report and take appropriate action based on your assessment."
                        + Constants.CONSTANT9 + Constants.CONSTANT9);
                byte[] fileData = pdfGeneratorService.export(reportId, expenseIds, response,"Manager");
                ByteArrayResource resource = new ByteArrayResource(fileData);
                eMail.addAttachment(Constants.CONSTANT6, resource);
                javaMailSender.send(message);
            } else {
                throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
            }
        } else {
            throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
        }
    }



    @Override
    public void userApprovedNotification(Long reportId, List<Long> expenseIds, HttpServletResponse response) throws IOException, MessagingException {
        Reports report = reportsService.getReportById(reportId);
        List<Expense> expenseList = expenseService.getExpenseByReportId(reportId);
        if (!expenseList.isEmpty()) {
            Expense expense = expenseList.get(0);
            Employee employee = expense.getEmployee();
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper eMail = new MimeMessageHelper(message, true);
            eMail.setFrom(Constants.CONSTANT9);
            eMail.setTo(employee.getEmployeeEmail());
            eMail.setSubject("[APPROVED] Expense Report: " + report.getReportTitle());
            eMail.setText(Constants.CONSTANT12 + employee.getFirstName() + " " + employee.getLastName() + ","
                    + "\n\nCongratulations! Your expense report has been approved by your manager. The details of the approval are as follows:"
                    + Constants.CONSTANT6 + report.getReportTitle() + "\nApproved Amount: ₹" + report.getTotalApprovedAmount()
                    + "\nApproval Date: " + report.getManagerActionDate()
                    + "\n\nIf you have any questions or need further assistance, please feel free to contact your manager or the HR department."
                    + Constants.CONSTANT12 + Constants.CONSTANT9 + Constants.CONSTANT12 + Constants.CONSTANT9);

            byte[] fileData = pdfGeneratorService.export(reportId, expenseIds, response,"Employee");
            ByteArrayResource resource = new ByteArrayResource(fileData);
            eMail.addAttachment(Constants.CONSTANT6, resource);
            this.javaMailSender.send(message);
        } else {
            throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
        }
    }

    @Override
    public void userReimbursedNotification(Long reportId) {
        Reports report = reportsService.getReportById(reportId);
        List<Expense> expenseList = expenseService.getExpenseByReportId(reportId);
        if (!expenseList.isEmpty()) {
            Expense expense = expenseList.get(0);
            Employee employee = expense.getEmployee();
            SimpleMailMessage eMail = new SimpleMailMessage();
            eMail.setFrom(Constants.CONSTANT9);
            eMail.setTo(employee.getEmployeeEmail());
            eMail.setSubject("[PUSHED TO REIMBURSEMENT] Expense Report: " + report.getReportTitle());
            eMail.setText(Constants.CONSTANT12 + employee.getFirstName() + " " + employee.getLastName() + ","
                    + "\n\nCongratulations! Your expense report has been pushed to reimbursement by the finance department. The details of the reimbursement are as follows:"
                    + Constants.CONSTANT6 + report.getReportTitle() + "\n\nAmount Reimbursed: ₹" + report.getTotalApprovedAmount()
                    + "\nReimbursement Date: " + report.getFinanceActionDate()
                    + "\n\nPlease check your bank account for the credited amount. If you have any questions or concerns regarding the reimbursement, please reach out to the finance department."
                    + Constants.CONSTANT12 + Constants.CONSTANT9 + Constants.CONSTANT12 + Constants.CONSTANT9);

            this.javaMailSender.send(eMail);
        } else {
            throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
        }
    }

    @Override
    public void userRejectedByFinanceNotification(Long reportId) {
        Reports report = reportsService.getReportById(reportId);
        List<Expense> expenseList = expenseService.getExpenseByReportId(reportId);

        if (!expenseList.isEmpty()) {
            Expense expense = expenseList.get(0);
            Employee employee = expense.getEmployee();
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(Constants.CONSTANT9);
            email.setTo(employee.getEmployeeEmail());
            email.setSubject("[REJECTED] Expense Report: " + report.getReportTitle());
            email.setText(Constants.CONSTANT12 + employee.getFirstName() + " " + employee.getLastName() + ","
                    + "\n\nWe regret to inform you that your expense report has been rejected by the finance department. The details of the rejection are as follows:"
                    + Constants.CONSTANT6 + report.getReportTitle() + "\n\nRejection Reason: " + report.getFinanceComments()
                    + "\nRejection Date: " + report.getFinanceActionDate()
                    + "\n\nPlease review the feedback provided by the finance department and make the necessary revisions to your expense report. Once you have made the required changes, resubmit the report for further processing."
                    + "\n\nIf you have any questions or need clarification regarding the rejection, please reach out to the finance department or your manager."
                    + "\n\nThank you for your understanding and cooperation." + Constants.CONSTANT9 + Constants.CONSTANT12 + Constants.CONSTANT9);

            this.javaMailSender.send(email);
        } else {
            throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
        }
    }

    @Override
    public void financeNotification(Long reportId, List<Long> expenseIds, HttpServletResponse response)
            throws IOException, MessagingException {
        Reports report = reportsService.getReportById(reportId);
        List<Expense> expenseList = expenseService.getExpenseByReportId(reportId);
        Employee admin = employeeRepository.findByRole("FINANCE_ADMIN");
        String adminEmail = admin.getEmployeeEmail();

        if (!expenseList.isEmpty()) {
            Expense expense = expenseList.get(0);
            Employee employee = expense.getEmployee();
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper eMail = new MimeMessageHelper(message, true);
            eMail.setFrom(Constants.CONSTANT9);
            eMail.setTo(adminEmail);
            eMail.setSubject("Expense Reimbursement Request: " + report.getReportTitle());
            eMail.setText(Constants.CONSTANT12 + admin.getFirstName() + ","
                    + "\n\nYou have received a request for expense reimbursement from employee "
                    + employee.getFirstName() + " " + employee.getLastName()
                    + ". The details of the expense report are as follows:" + Constants.CONSTANT6 + report.getReportTitle()
                    + "\nEmployee: " + employee.getFirstName() + " " + employee.getLastName() + Constants.CONSTANT9
                    + report.getTotalApprovedAmount() + " "
                    + "\n\nPlease review the attached expense report and process the reimbursement accordingly. If there are any additional details required or if you have any questions, please reach out to the employee or the HR department."
                    + "\n\nThank you for your attention to this matter." + Constants.CONSTANT9 + Constants.CONSTANT12 + Constants.CONSTANT9);

            byte[] fileData = pdfGeneratorService.export(reportId, expenseIds, response,"Finance");
            ByteArrayResource resource = new ByteArrayResource(fileData);
            eMail.addAttachment(Constants.CONSTANT6, resource);
            this.javaMailSender.send(message);
        } else {
            throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
        }
    }


    @Override
    public void userPartialApprovedExpensesNotification(Long reportId) {
        Reports report = reportsService.getReportById(reportId);
        List<Expense> expenseList = expenseService.getExpenseByReportId(reportId);

        if (!expenseList.isEmpty()) {
            Expense expense = expenseList.get(0);
            Employee employee = expense.getEmployee();
            SimpleMailMessage eMail = new SimpleMailMessage();
            eMail.setFrom(Constants.CONSTANT9);
            eMail.setTo(employee.getEmployeeEmail());
            eMail.setSubject("[PARTIAL APPROVAL] Expense Report: " + report.getReportTitle());
            eMail.setText(Constants.CONSTANT12 + employee.getFirstName() + " " + employee.getLastName() + ","
                    + "\n\nCongratulations! Your expense report has been partially approved by your manager. The details of the partial approval are as follows:"
                    + Constants.CONSTANT6 + report.getReportTitle() + "\nApproval Date: " + report.getManagerActionDate()
                    + "\n\nPlease note that not all expenses have been approved. Some expenses are partially approved according to the company policy. Please login to your BillFold account for detailed information of your expense report."
                    + "\n\nIf you have any questions or need further assistance, please feel free to contact your manager or the HR department."
                    + Constants.CONSTANT12 + Constants.CONSTANT9 + Constants.CONSTANT12 + Constants.CONSTANT9);

            this.javaMailSender.send(eMail);
        } else {
            throw new IllegalStateException(Constants.CONSTANT9 + report.getReportTitle());
        }
    }

    public void reminderMailToEmployee(List<Long> expenseIds) {
        for (Long expenseId : expenseIds) {
            Optional<Expense> expenseOptional = expenseRepository.findById(expenseId);

            if (expenseOptional.isPresent()) {
                Expense expense = expenseOptional.get();
                Employee employee = expense.getEmployee();
                if (employee != null) {
                    SimpleMailMessage email = new SimpleMailMessage();
                    email.setFrom(Constants.CONSTANT9);
                    email.setTo(employee.getEmployeeEmail());
                    email.setSubject("[ACTION REQUIRED] Expense Report Submission Reminder");
                    email.setText(Constants.CONSTANT12 + employee.getFirstName() + " " + employee.getLastName() + ","
                            + "\n\nWe hope this email finds you well. We would like to remind you that you have not yet reported some of your expenses.It is important to submit your expense report in a timely manner to ensure accurate reimbursement and compliance with company policies."
                            + "\n\nPlease note that if your expenses are not reported within 60 days from the end of the reporting period, they will not be eligible for reimbursement. Therefore, we kindly request you to submit your expense report by the submission deadline. This will allow us to review and process your expenses promptly."
                            + "\n\nTo report your expenses, please access your BillFold account and follow the instructions provided. Ensure that all receipts and necessary supporting documents are attached for proper validation."
                            + "\n\nIf you have any questions or need assistance with the expense reporting process, please reach out to our HR department or your manager. We are here to help and ensure a smooth reimbursement process for you."
                            + "Thank you for your attention to this matter. Your cooperation in submitting your expense report within the specified deadline is greatly appreciated."
                            + Constants.CONSTANT9 + Constants.CONSTANT12 + Constants.CONSTANT9);

                    this.javaMailSender.send(email);
                }
            }
        }
    }

    @Override
    public void reminderMailToManager(List<Long> reportIds) {

        for (Long reportId : reportIds) {
            Optional<Reports> reportOptional = reportsRepository.findById(reportId);
            if (reportOptional.isPresent()) {
                Reports report = reportOptional.get();
                Long employeeId = report.getEmployeeId();
                Employee employee = employeeService.getEmployeeById(employeeId);
                SimpleMailMessage email = new SimpleMailMessage();
                email.setFrom(Constants.CONSTANT9);
                email.setTo(employee.getManagerEmail());
                email.setSubject("[REMINDER] Pending Action on Expense Reports");
                email.setText(Constants.CONSTANT12 + employee.getManagerName() + ","
                        + "\n\nThis is a friendly reminder that you have pending expense reports awaiting your action. The reports have been submitted more than 30 days ago and are still pending approval."
                        + "\n\nPlease review and take appropriate action on the following report:" + "\n\n"
                        + report.getReportTitle()
                        + "\n\nTo take action on these reports, please log in to the BillFold app."
                        + "\n\nThank you for your attention to this matter." + Constants.CONSTANT9 + Constants.CONSTANT12 + Constants.CONSTANT9);
                this.javaMailSender.send(email);
            }
        }
    }
}