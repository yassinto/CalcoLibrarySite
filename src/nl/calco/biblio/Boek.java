package nl.calco.biblio;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

public class Boek implements Serializable { // Book class

	// Attributes as found in database
	private String boekNummer;  	// bookNumber
	private int nummer; 			// digit part of bookNumber - necessary for establishing next booknumber
	private String titel; 			// title
	private String auteur; 			// author
	private String isbn; 			// ISBN number
	private Categorie categorie; 	// category
	private String locatie; 		// location
	private String uitgeverij; 		// publisher
	private Integer boekId; 		// Unique key attribute

	private int aantalExemplaren; 		// Amount of copies linked to bookId
	private List<Exemplaar> exemplaren; // List of copies linked to bookId

	// Getters & Setters
	public String getBoekNummer() {
		return boekNummer;
	}

	public void setBoekNummer(String boekNummer) {
		this.boekNummer = boekNummer;
		nummer = Integer.parseInt(boekNummer.substring(2).trim());
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public int getNummer() {
		return nummer;
	}

	public void setNummer(int nummer) {
		this.nummer = nummer;
		String boekNummer = "CA" + nummer;
		setBoekNummer(boekNummer);
	}

	public String getLocatie() {
		return locatie;
	}

	public void setLocatie(String locatie) {
		this.locatie = locatie;
	}

	public String getUitgeverij() {
		return uitgeverij;
	}

	public void setUitgeverij(String uitgeverij) {
		this.uitgeverij = uitgeverij;
	}

	public Integer getBoekId() {
		return boekId;
	}

	public void setBoekId(Integer boekId) {
		this.boekId = boekId;
	}

	public int getAantalExemplaren() {
		return aantalExemplaren;
	}

	public void setAantalExemplaren(int aantalExemplaren) {
		this.aantalExemplaren = aantalExemplaren;
	}

	public List<Exemplaar> getExemplaren() {
		return exemplaren;
	}

	public void setExemplaren(List<Exemplaar> exemplaren) {
		this.exemplaren = exemplaren;
		setAantalExemplaren(exemplaren.size());
	}
}
