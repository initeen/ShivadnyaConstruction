package com.shivadnyaconstruction.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;

@Service
public class InquiryReportScheduler {

	private final JavaMailSender mailSender;

	@Value("${site.mail.to}")
	private String toEmail;

	public InquiryReportScheduler(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	// Run at 8am, 10am, 2pm, 5pm every day
	@Scheduled(cron = "0 0 8,10,14,17 * * ?")
	public void sendDailyReport() {
		try {
			File csv = new File("data/inquiries.csv");
			if (!csv.exists()) {
				System.out.println("No inquiries yet, skipping report.");
				return;
			}

			// Parse CSV safely
			try (Reader reader = Files.newBufferedReader(csv.toPath(), StandardCharsets.UTF_8);
					CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreSurroundingSpaces()
							.parse(reader)) {

				File excelFile = generateExcel(parser);

				// Send email
				sendEmailWithAttachment(excelFile);

				// Optional: delete after sending
				excelFile.delete();

				System.out.println("Report sent successfully at " + LocalDate.now());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private File generateExcel(CSVParser parser) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Inquiries");

		// Header row
		Row headerRow = sheet.createRow(0);
		int colIndex = 0;
		for (String header : parser.getHeaderNames()) {
			headerRow.createCell(colIndex++).setCellValue(header);
		}

		// Data rows
		int rowIndex = 1;
		for (CSVRecord record : parser) {
			Row row = sheet.createRow(rowIndex++);
			colIndex = 0;
			for (String header : parser.getHeaderNames()) {
				row.createCell(colIndex++).setCellValue(record.get(header));
			}
		}

		// Autosize columns
		for (int i = 0; i < parser.getHeaderNames().size(); i++) {
			sheet.autoSizeColumn(i);
		}

		File file = new File("data/inquiries-" + LocalDate.now() + ".xlsx");
		try (FileOutputStream fos = new FileOutputStream(file)) {
			workbook.write(fos);
		}
		workbook.close();
		return file;
	}

	private void sendEmailWithAttachment(File excelFile) throws Exception {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(toEmail);
		helper.setSubject("Daily Inquiry Report - " + LocalDate.now());
		helper.setText("Attached is the inquiry report for " + LocalDate.now());

		helper.addAttachment(excelFile.getName(), excelFile);

		mailSender.send(mimeMessage);
	}

}
