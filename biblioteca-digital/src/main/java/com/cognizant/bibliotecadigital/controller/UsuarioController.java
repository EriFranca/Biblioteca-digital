package com.cognizant.bibliotecadigital.controller;

import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cognizant.bibliotecadigital.model.Papel;
import com.cognizant.bibliotecadigital.model.Usuario;
import com.cognizant.bibliotecadigital.security.SecurityConfig;
import com.cognizant.bibliotecadigital.service.UsuarioService;

@Controller
@RequestMapping
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@GetMapping("/login")
	public ModelAndView login(@RequestParam(name = "error", required = false, defaultValue = "") String erro) {
		ModelAndView login = new ModelAndView("login/Login");
		if (erro.equals("erroLogin")) {
			login.addObject("msgErro", "Email ou Senha incorreta");
		}

		return login;
	}
	
	@GetMapping("/usuarios")
	public ModelAndView findAll() {
		ModelAndView mv = new ModelAndView("/usuario/usuario");
		mv.addObject("usuarios", usuarioService.findAll());

		return mv;
	}

	@GetMapping("/register")
	public ModelAndView register() {
		ModelAndView modelAndView = new ModelAndView("register/Register");
		modelAndView.addObject("usuario", new Usuario());
		return modelAndView;
	}

	

	@PostMapping("/register/create")
	public ModelAndView create(@Valid @ModelAttribute Usuario usuario, BindingResult bindingRes,
			RedirectAttributes redAttributes) {

		if (bindingRes.hasErrors()) {
			return register();
		} 
		
		usuario.setSenha(SecurityConfig.bcryptPasswordEncoder().encode(usuario.getSenha()));
		usuario.setPapeis(new LinkedHashSet<>(Arrays.asList(new Papel("ROLE_COMUM"))));
		redAttributes.addFlashAttribute("mensagem", "Usuario Cadastrado com sucesso!!");
		usuarioService.save(usuario);

		ModelAndView mv = new ModelAndView("redirect:/login");
		return mv;
	}

	@GetMapping("/usuarios/{id}")
	public ModelAndView detail(@PathVariable("id") Long id) {
		ModelAndView mv = new ModelAndView("/usuario/usuario");
		mv.addObject("usuario", usuarioService.findById(id));

		return mv;
	}

	/*
	 * Alteração Bruno TODO : Usuario altera seu perfil ( dados pessoais)
	 * 
	 * @GetMapping("/usuarios/perfil") public ModelAndView novo() { ModelAndView
	 * modelAndView = new ModelAndView("/pessoa/pessoa_form");
	 * modelAndView.addObject("usuario", new Usuario()); return modelAndView; }
	 */

}