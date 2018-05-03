package com.cognizant.bibliotecadigital.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.bibliotecadigital.model.Autor;
import com.cognizant.bibliotecadigital.model.Livro;
import com.cognizant.bibliotecadigital.repository.LivroRepository;

@Service
public class LivroService {

	@Autowired
	private LivroRepository livroRepository;
	
	//Cadastrar livro
	public Livro save(Livro livro){
		return livroRepository.save(livro);
	}
	
	public Iterable<Livro> findAll() {
		return livroRepository.findAll();
	}
	
}
