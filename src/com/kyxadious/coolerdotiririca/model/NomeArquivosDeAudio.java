package com.kyxadious.coolerdotiririca.model;

import java.util.ArrayList;

public class NomeArquivosDeAudio {

	private Audios audios;
	
	public NomeArquivosDeAudio() {
		audios = new Audios();
	}
	
	public ArrayList<String> getListaNomes() {
		String nomeAudio;
		ArrayList<Audio> arrayListAudios = audios.getTodosAudios();
		ArrayList<String> arrayListNomeArquivosAudio = new ArrayList<String>();
		
		for (int i=0; i<arrayListAudios.size(); i++) {
			nomeAudio = arrayListAudios.get(i).getNome();
			arrayListNomeArquivosAudio.add(nomeAudio);
		}
		
		return arrayListNomeArquivosAudio; 
	}
	
	
}
