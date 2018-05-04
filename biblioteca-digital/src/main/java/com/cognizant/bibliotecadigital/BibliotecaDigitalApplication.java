package com.cognizant.bibliotecadigital;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.cognizant.bibliotecadigital.model.Autor;
import com.cognizant.bibliotecadigital.model.CategoriaLivro;
import com.cognizant.bibliotecadigital.model.Livro;
import com.cognizant.bibliotecadigital.model.UnidadeLivro;
import com.cognizant.bibliotecadigital.repository.AutorRepository;
import com.cognizant.bibliotecadigital.repository.CategoriaLivroRepository;
import com.cognizant.bibliotecadigital.repository.LivroRepository;
import com.cognizant.bibliotecadigital.repository.UnidadeLivroRepository;

@SpringBootApplication
public class BibliotecaDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotecaDigitalApplication.class, args);
	}
	
	
	//Bloco somente deve ser executado para popular o banco numa primeira execução 
	/*
	@Bean
	public CommandLineRunner mock(CategoriaLivroRepository catRepo, AutorRepository autRepo, LivroRepository livRepo, UnidadeLivroRepository unidadeRepo) {
		return (String[] args) -> {
			
			catRepo.save(new CategoriaLivro(0L, "categoria1"));
			catRepo.save(new CategoriaLivro(0L, "categoria2"));
			
			autRepo.save(new Autor(0L, "autor1"));
			autRepo.save(new Autor(0L, "autor2"));
			autRepo.save(new Autor(0L, "autor3"));
			
			Livro livro = new Livro(0L, "1234567890123", "titulo", 2000,
					2, "sinopse", "foto", "Autor do livro",
					new HashSet<>(), new ArrayList<>(),
					new ArrayList<>());
			
			livro.getCategoriaLivros().add(catRepo.findById(1L).get());
			livro.getCategoriaLivros().add(catRepo.findById(2L).get());
			// livro.getUnidadeLivros().add(new UnidadeLivro());
			
			Livro salvo = livRepo.save(livro);
			unidadeRepo.save(new UnidadeLivro(0L, null, livRepo.findById(salvo.getId()).get()));
		};
	}
	/**/
}
