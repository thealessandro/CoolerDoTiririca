package com.kyxadious.coolerdotiririca.model;

public class Audio {

	private String nome;
	private String nomeArquivo;

	public Audio() {

	}

	public Audio(String nome, String nomeArquivo) {
		this.nome = nome;
		this.nomeArquivo = nomeArquivo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

}
