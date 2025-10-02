package com.shivadnyaconstruction.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.shivadnyaconstruction.model.ContactRequest;

@Service
public class ContactService {

	private final JavaMailSender mailSender;

	@Value("${site.mail.to}")
	private String toEmail;

	public ContactService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void recordInquiry(ContactRequest r) {
		// 1) Append to CSV (data/inquiries.csv)
		File dir = new File("data");
		if (!dir.exists())
			dir.mkdirs();
		File csv = new File(dir, "inquiries.csv");
		boolean writeHeader = !csv.exists();
		try (FileWriter fw = new FileWriter(csv, true)) {
			if (writeHeader) {
				fw.write("timestamp,name,email,phone,subject,preferredContactTime,message\n");
			}
			String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", ts, esc(r.getName()),
					esc(r.getEmail()), esc(r.getPhone()), esc(nullToEmpty(r.getSubject())),
					esc(nullToEmpty(r.getPreferredContactTime())), esc(r.getMessage()));
			fw.write(line);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write inquiry CSV", e);
		}

		// 2) Email to you
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(toEmail);
		msg.setSubject("New Inquiry — " + (r.getSubject() != null ? r.getSubject() : "(no subject)"));
		msg.setText("Name: " + r.getName() + "\n" + "Email: " + r.getEmail() + "\n" + "Phone: " + r.getPhone() + "\n"
				+ "Preferred time: " + nullToEmpty(r.getPreferredContactTime()) + "\n\n" + r.getMessage());
		mailSender.send(msg);
	}

	private String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

	private String esc(String s) {
		return s.replace("\"", "' ");
	}
}
