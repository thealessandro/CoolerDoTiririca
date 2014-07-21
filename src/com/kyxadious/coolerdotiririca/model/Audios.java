package com.kyxadious.coolerdotiririca.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Audios {
	
	private HashMap<String, Audio> hashMapAudios;
	
	public Audios() {
		alimentarDados();
	}
	
	
	private void alimentarDados(){
		hashMapAudios = new HashMap<String, Audio>();
		hashMapAudios.put("Discola parasita", new Audio("Discola parasita", "discola-parasita.mp3"));
		hashMapAudios.put("Eu adoro balada", new Audio("Eu adoro balada", "eu-adoro-balada.mp3"));
		hashMapAudios.put("Excelentíssimo abestado", new Audio("Excelentíssimo abestado", "excelentissimo-abestado.mp3"));
		hashMapAudios.put("Fica sendo mandado", new Audio("Fica sendo mandado", "fica-sendo-mandado.mp3"));
		hashMapAudios.put("Larga essa mocreia", new Audio("Larga essa mocreia", "larga-essa-mocreia.mp3"));
		hashMapAudios.put("Levaa euuuu", new Audio("Levaa euuuu", "levaa-euuuu.mp3"));
		hashMapAudios.put("Leva eu", new Audio("Leva eu", "leva-eu.mp3"));
		hashMapAudios.put("Mulher veia ai", new Audio("Mulher veia ai", "mulher-veia-ai.mp3"));
		hashMapAudios.put("Ramo pra balada", new Audio("Ramo pra balada", "ramo-pra-balada.mp3"));
		hashMapAudios.put("Ramo pra balada completo", new Audio("Ramo pra balada completo", "ramo-pra-balada-completo.mp3"));
	}
	
	public ArrayList<Audio> getTodosAudios() {
		ArrayList<Audio> listaAudios = new ArrayList<Audio>(hashMapAudios.values());	
		return listaAudios;
	}
	
	public String getNomeArquivoAudio(String key) {
		Audio audio = hashMapAudios.get(key);
		String nome = audio.getNomeArquivo();
		return nome;
	}

}
