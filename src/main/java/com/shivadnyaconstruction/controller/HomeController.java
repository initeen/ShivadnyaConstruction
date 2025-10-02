package com.shivadnyaconstruction.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home(Model model) {
		//model.addAttribute("page", "home");
		return "index";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("page", "about");
		return "about";
	}

	@GetMapping("/services")
	public String services(Model model) {
		model.addAttribute("page", "services");
		return "services";
	}

	@GetMapping("/projects")
	public String projects(Model model) {
		model.addAttribute("page", "projects");
		return "projects";
	}

	@GetMapping("/gallery")
	public String gallery(Model model) {
		model.addAttribute("page", "gallery");
		return "gallery";
	}

	@GetMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("page", "contact");
		return "contact";
	}

	@GetMapping("/privacy")
	public String privacy() {
		return "privacy";
	}
}
