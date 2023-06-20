package com.nineleaps.expensemanagementproject.service;

import java.awt.Color;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.nineleaps.expensemanagementproject.entity.Employee;
import com.nineleaps.expensemanagementproject.entity.Expense;
import com.nineleaps.expensemanagementproject.entity.Reports;
import com.nineleaps.expensemanagementproject.repository.EmployeeRepository;
import com.nineleaps.expensemanagementproject.repository.ExpenseRepository;
import com.nineleaps.expensemanagementproject.repository.ReportsRepository;

@Service
public class PdfGeneratorServiceImpl implements IPdfGeneratorService {
	@Autowired
	ExpenseRepository expenseRepository;
	@Autowired
	ReportsRepository reportsRepository;
	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	public byte[] generatePdf(Long reportId) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4);
		@SuppressWarnings("unused")
		PdfWriter writer = PdfWriter.getInstance(document, baos);
		class FooterEvent extends PdfPageEventHelper {
			public void onEndPage(PdfWriter writer, Document document) {
				PdfContentByte pdfContentByte = writer.getDirectContent();
				float x = PageSize.A4.getWidth() - document.rightMargin();
				float y = document.bottomMargin() - 20;
				Font billFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 25, Font.NORMAL, Color.BLACK);
				Font foldFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 25, Font.NORMAL, new Color(0, 0, 139));
				Font generatedbyFont = FontFactory.getFont(FontFactory.TIMES, 10, Font.NORMAL, Color.BLACK);
				Paragraph footerParagraph = new Paragraph();
				footerParagraph.setAlignment(Element.ALIGN_RIGHT);
				footerParagraph.add(new Chunk("Generated By ", generatedbyFont));
				footerParagraph.add(new Chunk("Bill", billFont));
				footerParagraph.add(new Chunk("Fold.", foldFont));
				ColumnText.showTextAligned(pdfContentByte, Element.ALIGN_RIGHT, footerParagraph, x, y, 0);
			}
		}
		FooterEvent event = new FooterEvent();
		writer.setPageEvent(event);

		document.open();
		Font fontHeader = FontFactory.getFont(FontFactory.TIMES);
		fontHeader.setSize(22);
		Paragraph headerParagraph = new Paragraph("BillFold - Expense Report", fontHeader);
		headerParagraph.setAlignment(Paragraph.ALIGN_CENTER);
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		Font font = FontFactory.getFont(FontFactory.TIMES, 14);
		table.addCell(getCenterAlignedCell("Date", font));
		table.addCell(getCenterAlignedCell("Merchant", font));
		table.addCell(getCenterAlignedCell("Description", font));
		table.addCell(getCenterAlignedCell("Amount", font));
		Font fontParagraph = FontFactory.getFont(FontFactory.TIMES);
		fontParagraph.setSize(14);
		Reports report = reportsRepository.findById(reportId).get();
		List<Expense> expenses = expenseRepository.findByReports(report);
		Employee employee = null;
		if (!expenses.isEmpty()) {
			Expense firstExpense = expenses.get(0);
			employee = employeeRepository.findById(firstExpense.getEmployee().getEmployeeId()).orElse(null);
		}
		float total = 0;
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MMM d, yyyy");
		for (Expense expense : expenses) {
			LocalDate dateCreated = expense.getDate();
			table.addCell(getCenterAlignedCells(dateCreated.format(formatter1), font));
			table.addCell(getCenterAlignedCells(expense.getMerchantName(), font));
			table.addCell(getCenterAlignedCells(expense.getDescription(), font));
			table.addCell(getCenterAlignedCells(expense.getAmount().toString(), font));
			total += expense.getAmount();
		}

		Font fontParagraph1 = FontFactory.getFont(FontFactory.TIMES_BOLD);
		fontParagraph1.setSize(14);
		Chunk currencyChunk = new Chunk(report.getCurrency(), fontParagraph1);
		Chunk totalChunk = new Chunk(String.valueOf(total), fontParagraph1);
		Paragraph pdfParagraph01 = new Paragraph();
		pdfParagraph01.setAlignment(Paragraph.ALIGN_RIGHT);
		pdfParagraph01.add("Total Amount: ");
		pdfParagraph01.add(currencyChunk);
		pdfParagraph01.add(" ");
		pdfParagraph01.add(totalChunk);

		Font fontParagraph11 = FontFactory.getFont(FontFactory.TIMES);
		fontParagraph11.setSize(14);
		@SuppressWarnings("null")
		Paragraph pdfParagraph = new Paragraph(
				"Employee Name : " + employee.getFirstName() + " " + employee.getLastName(), fontParagraph);
		pdfParagraph.setAlignment(Paragraph.ALIGN_LEFT);
		Font fontParagraph12 = FontFactory.getFont(FontFactory.TIMES);
		fontParagraph12.setSize(14);
		Paragraph pdfParagraph02 = new Paragraph("Employee Email : " + employee.getEmployeeEmail(), fontParagraph);
		pdfParagraph02.setAlignment(Paragraph.ALIGN_LEFT);
		Paragraph emptyParagraph = new Paragraph(" ");
		Paragraph emptyParagraph01 = new Paragraph(" ");
		Paragraph emptyParagraph02 = new Paragraph(" ");
		Font fontParagraph13 = FontFactory.getFont(FontFactory.TIMES);
		fontParagraph13.setSize(20);
		Paragraph pdfParagraph03 = new Paragraph(report.getReportTitle(), fontParagraph13);
		pdfParagraph03.setAlignment(Paragraph.ALIGN_LEFT);
		pdfParagraph03.add(report.getCurrency());
		Paragraph pdfParagraph011 = new Paragraph();
		pdfParagraph011.setAlignment(Paragraph.ALIGN_RIGHT);
		pdfParagraph011.add("Total Amount: ");
		pdfParagraph011.add(currencyChunk);
		pdfParagraph011.add(" ");
		pdfParagraph011.add(totalChunk);
		Font fontNote = FontFactory.getFont(FontFactory.TIMES_ITALIC, 10);
		Paragraph noteParagraph = new Paragraph("Notes:\n", fontNote);
		noteParagraph.setAlignment(Paragraph.ALIGN_LEFT);
		noteParagraph.setIndentationLeft(20);
		noteParagraph.add("The information on this receipt was manually entered. Please verify for authenticity.\n");
		Paragraph lineSeparator = new Paragraph(
				"----------------------------------------------------------------------------------------------------------------------------------");
		lineSeparator.setAlignment(Element.ALIGN_CENTER);
		lineSeparator.setSpacingAfter(10);
		Paragraph emptyParagraph03 = new Paragraph(" ");
		Paragraph emptyParagraph04 = new Paragraph(" ");
		Paragraph emptyParagraph05 = new Paragraph(" ");
		Paragraph emptyParagraph06 = new Paragraph(" ");
		Paragraph emptyParagraph07 = new Paragraph(" ");
		Paragraph emptyParagraph08 = new Paragraph(" ");
		Paragraph historyTitle = new Paragraph("Report History and comments:",
				FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
		historyTitle.setAlignment(Element.ALIGN_LEFT);
		historyTitle.setSpacingAfter(10);
		Paragraph historyContent = new Paragraph();
		historyContent.setAlignment(Element.ALIGN_LEFT);
		historyContent.setFont(FontFactory.getFont(FontFactory.TIMES, 10));

		LocalDate dateCreated = report.getDateCreated();
		LocalDate dateSubmitted = report.getDateSubmitted();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

		String createdMessage = "Report Created on:\n" + dateCreated.format(formatter);
		historyContent.add(createdMessage);
		historyContent.add(Chunk.NEWLINE);

		String submissionMessage = "Report submitted to you (cc: you) on:\n" + dateSubmitted.format(formatter);
		historyContent.add(submissionMessage);

		Font fontParagraph14 = FontFactory.getFont(FontFactory.TIMES_ITALIC);
		fontParagraph14.setSize(14);
		Paragraph pdfParagraph04 = new Paragraph(report.getReportDescription(), fontParagraph14);
		pdfParagraph04.setAlignment(Paragraph.ALIGN_LEFT);


		String qrCodeData = "Employee Email=" + employee.getEmployeeEmail() + " " + "Employee Name="
				+ employee.getFirstName() + " " + "TotalAmount=" + currencyChunk + totalChunk;
		int qrCodeSize = 200;
		Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix;
		try {
			bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);
		} catch (WriterException e) {
			e.printStackTrace();

			return null;
		}
		BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

		ByteArrayOutputStream qrBaos = new ByteArrayOutputStream();
		ImageIO.write(qrImage, "png", qrBaos);
		qrBaos.flush();
		byte[] qrBytes = qrBaos.toByteArray();
		qrBaos.close();

		Image qrCodeImage = Image.getInstance(qrBytes);
		qrCodeImage.setAlignment(Element.ALIGN_CENTER);
		qrCodeImage.scaleToFit(150, 150);

		document.add(headerParagraph);

		document.add(emptyParagraph01);
		document.add(emptyParagraph02);
		document.add(pdfParagraph03);
		document.add(pdfParagraph04);
		document.add(pdfParagraph);
		document.add(pdfParagraph02);
		document.add(emptyParagraph);
		document.add(emptyParagraph07);
		document.add(emptyParagraph08);
		document.add(table);
		document.add(pdfParagraph011);
		document.add(emptyParagraph03);
		document.add(emptyParagraph04);
		document.add(emptyParagraph05);
		document.add(emptyParagraph06);
		document.add(noteParagraph);
		document.add(lineSeparator);
		document.add(historyTitle);
		document.add(historyContent);
		document.add(qrCodeImage);


		document.newPage();
		for (Expense expense : expenses) {
			if(expense.getSupportingDocuments()==null)
				continue;
			if (expense.getSupportingDocuments() != null ) {
				byte[] imageData = expense.getSupportingDocuments();
				InputStream in = new ByteArrayInputStream(imageData);
				BufferedImage bufferedImage = ImageIO.read(in);
				Image image = Image.getInstance(bufferedImage, null);
				image.scaleAbsolute(600f, 600f);
				image.setAlignment(Image.MIDDLE);
				document.add(image);
				document.newPage();
			}
		}

		document.close();
		writer.close();

		return baos.toByteArray();
	}

	@Override
	public PdfPCell getCenterAlignedCell(String content, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(content, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(Element.ALIGN_MIDDLE);
		cell.setBackgroundColor(new Color(240, 240, 240));
		cell.setBorderWidth(0.0f);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		return cell;
	}

	@Override
	public PdfPCell getCenterAlignedCells(String content, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(content, font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(Element.ALIGN_MIDDLE);
		cell.setBorderWidth(0.01f);
		cell.setBorderWidthLeft(0);
		cell.setBorderWidthRight(0);
		return cell;
	}

	@Override
	public void export(Long reportId, HttpServletResponse response) throws IOException {
		byte[] pdfBytes = generatePdf(reportId);
		Reports report = reportsRepository.findById(reportId).get();
		report.setPdfFile(pdfBytes);
		reportsRepository.save(report);
		FileOutputStream fileOutputStream = new FileOutputStream("expense_report.pdf");
		fileOutputStream.write(pdfBytes);
		fileOutputStream.flush();
		fileOutputStream.close();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + "expense_report_BillFold.pdf" + "\"");
		response.setContentLength(pdfBytes.length);
		ServletOutputStream outputStream = response.getOutputStream();
		outputStream.write(pdfBytes);
		outputStream.flush();
		outputStream.close();
	}

}