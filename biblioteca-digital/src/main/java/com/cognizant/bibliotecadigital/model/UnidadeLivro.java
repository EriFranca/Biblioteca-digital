package com.cognizant.bibliotecadigital.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="unidadeLivro")
public class UnidadeLivro implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="unidadeLivro_seq", sequenceName="unidadeLivro_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="unidadeLivro_seq")
	@Column(name="id")
	private Long id;
	
	@Column(name="avarias")
	private String avarias;
	
	@ManyToOne
	@JoinColumn(name="livro_id")
	private Livro livro;
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((avarias == null) ? 0 : avarias.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((livro == null) ? 0 : livro.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnidadeLivro other = (UnidadeLivro) obj;
		if (avarias == null) {
			if (other.avarias != null)
				return false;
		} else if (!avarias.equals(other.avarias))
			return false;
		if (id != other.id)
			return false;
		if (livro == null) {
			if (other.livro != null)
				return false;
		} else if (!livro.equals(other.livro))
			return false;
		return true;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAvarias() {
		return avarias;
	}
	
	

	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public void setAvarias(String avarias) {
		this.avarias = avarias;
	}

	@Override
	public String toString() {
		return "UnidadeLivro [id=" + id + ", avarias=" + avarias + "]";
	}
	
}
