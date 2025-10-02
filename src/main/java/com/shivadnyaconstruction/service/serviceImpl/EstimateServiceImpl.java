package com.shivadnyaconstruction.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

import com.shivadnyaconstruction.dto.EstimateRequest;
import com.shivadnyaconstruction.dto.EstimateResponse;
import com.shivadnyaconstruction.service.EstimateService;

@Service
public class EstimateServiceImpl implements EstimateService {

	private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${site.mail.to}")
    private String adminEmail;

    @Value("${estimates.dir}")
    private String estimatesDir;

    public EstimateServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public EstimateResponse createAndSendEstimate(EstimateRequest req) throws Exception {
        // 1) calculate
        Breakdown breakdown = calculate(req);
        double total = breakdown.total;

        // 2) prepare thymeleaf context
        Context ctx = new Context();
        ctx.setVariable("req", req);
        ctx.setVariable("breakdown", breakdown);
        ctx.setVariable("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        // ✅ embed logo as base64
        try {
            ClassPathResource logoRes = new ClassPathResource("static/img/image.png");
            if (logoRes.exists()) {
                try (InputStream is = logoRes.getInputStream()) {
                    byte[] bytes = is.readAllBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    String dataUri = "data:image/png;base64," + base64;
                    ctx.setVariable("logoDataUri", dataUri);
                }
            }
        } catch (Exception ignore) {}

        String html = templateEngine.process("estimate-pdf", ctx);

        // 3) ensure dir
        Path dir = Paths.get(estimatesDir);
        Files.createDirectories(dir);

        // 4) write pdf
        String safeEmail = (req.email == null) ? "guest" : req.email.replaceAll("[^a-zA-Z0-9]", "");
        String fileName = String.format("estimate-%s-%d.pdf", safeEmail, System.currentTimeMillis());
        Path pdfPath = dir.resolve(fileName);
        try (OutputStream os = new FileOutputStream(pdfPath.toFile())) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
        }

        // 5) send emails (user + admin)
        String subjectUser = "Your Construction Estimate from Shivadnya Construction";
        String bodyUser = "Hello " + (req.name == null ? "" : req.name) + ",\n\nPlease find attached your detailed estimate.\n\nRegards,\nShivadnya Construction";
        sendEmailWithAttachment(req.email, subjectUser, bodyUser, pdfPath.toFile());

        String subjectAdmin = String.format("New Estimate: %s - %s", req.cityArea, req.email);
        String bodyAdmin = "New estimate submitted. Details attached.\n\nUser: " + req.name + " (" + req.email + ")\nGenerated at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        sendEmailWithAttachment(adminEmail, subjectAdmin, bodyAdmin, pdfPath.toFile());

        EstimateResponse resp = new EstimateResponse();
        resp.message = "Estimate generated and emailed.";
        resp.estimatedCost = total;
        resp.pdfFile = pdfPath.toAbsolutePath().toString();
        return resp;
    }

    private void sendEmailWithAttachment(String to, String subject, String body, File file) throws Exception {
        if (to == null || to.isBlank()) return; // skip if no recipient
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, false);
        FileSystemResource fr = new FileSystemResource(file);
        helper.addAttachment(fr.getFilename(), fr);
        mailSender.send(message);
    }

    // --- Calculation logic ---
    private Breakdown calculate(EstimateRequest r) {
        Breakdown b = new Breakdown();
        double slab = rangeToValue(r.slabRange);

        double basePerSqft = switch (r.material == null ? "Standard" : r.material) {
            case "Premium" -> 2000;
            case "Luxury" -> 3000;
            default -> 1500;
        };

        double qualityMult = switch (r.quality == null ? "Basic" : r.quality) {
            case "Medium" -> 1.15;
            case "High-end" -> 1.35;
            default -> 1.0;
        };

        double floorMult = 1.0 + (Math.max(1, r.floors) - 1) * 0.08;
        double workTypeMult = "Renovation".equalsIgnoreCase(r.workType) ? 0.85 : ("Commercial".equalsIgnoreCase(r.workType) ? 1.2 : 1.0);

        b.structure = slab * basePerSqft * qualityMult * floorMult * workTypeMult;

        Map<String, Object> s = r.selections == null ? Map.of() : r.selections;
        int doors = toInt(s.getOrDefault("doors", 3));
        int windows = toInt(s.getOrDefault("windows", 8));
        double paintArea = toDouble(s.getOrDefault("paintArea", slab));

        double doorUnit = 4000; double windowUnit = 2500; double paintPerSqft = 25; double puttyPerSqft = 10;
        double cementPerSqft = 80; double steelPerSqft = 120; double bricksPerSqft = 30;

        b.doorsCost = doors * doorUnit;
        b.windowsCost = windows * windowUnit;
        b.paintCost = paintArea * paintPerSqft;
        b.puttyCost = paintArea * puttyPerSqft;
        b.cementCost = slab * cementPerSqft;
        b.steelCost = slab * steelPerSqft;
        b.bricksCost = slab * bricksPerSqft;

        b.materialsTotal = b.doorsCost + b.windowsCost + b.paintCost + b.puttyCost + b.cementCost + b.steelCost + b.bricksCost;
        b.labour = b.materialsTotal * 0.18;

        double mandatory = r.includeConnections ? (25000 + 30000 + 15000) : 0;
        b.mandatoryFees = mandatory;

        double subtotal = b.structure + b.materialsTotal + b.labour + b.mandatoryFees;
        b.contingency = subtotal * 0.05;
        b.admin = 5000;
        b.total = subtotal + b.contingency + b.admin;

        return b;
    }

    private static int toInt(Object o) {
        try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; }
    }
    private static double toDouble(Object o) {
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0; }
    }
    private static double rangeToValue(String r) {
        if (r==null) return 1000;
        r = r.toLowerCase();
        if (r.contains("above")) {
            String n = r.replaceAll("[^0-9]", "");
            try { return Double.parseDouble(n) * 1.2; } catch(Exception e){return 600;}
        }
        if (r.contains("-")) {
            String[] p = r.split("-");
            try { double a = Double.parseDouble(p[0].replaceAll("[^0-9]","")); double b = Double.parseDouble(p[1].replaceAll("[^0-9]","")); return (a+b)/2.0; } catch(Exception e){return 1000;}
        }
        try { return Double.parseDouble(r.replaceAll("[^0-9]","")); } catch(Exception e) { return 1000; }
    }

    public static class Breakdown {
        public double structure;
        public double doorsCost;
        public double windowsCost;
        public double paintCost;
        public double puttyCost;
        public double cementCost;
        public double steelCost;
        public double bricksCost;
        public double materialsTotal;
        public double labour;
        public double mandatoryFees;
        public double contingency;
        public double admin;
        public double total;
    }
    
}