package nl.calco.biblio;

import java.io.Serializable;
import java.util.List;

public class Medewerker implements Serializable { // Employee class

	// Attributes as found in database
	private Integer medewerkerId; 	// Unique key
	private String naam; 			// Complete name
	private String voornaam; 		// First name
	private String tussenvoegsel; 	// Preposition
	private String achternaam; 		// Last name
	private String email; 			// Email
	private List<BoekBezit> boeken; // List of books in procession

	// Getters and Setters
	public Integer getMedewerkerId() {
		return medewerkerId;
	}

	public void setMedewerkerId(Integer medewerkerId) {
		this.medewerkerId = medewerkerId;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<BoekBezit> getBoeken() {
		return boeken;
	}

	public void setBoeken(List<BoekBezit> boeken) {
		this.boeken = boeken;
	}

	public String getVoornaam() {
		return voornaam;
	}

	public void setVoornaam(String voornaam) {
		this.voornaam = voornaam;
	}

	public String getAchternaam() {
		return achternaam;
	}

	public void setAchternaam(String achternaam) {
		this.achternaam = achternaam;
	}

	public String getTussenvoegsel() {
		return tussenvoegsel;
	}

	public void setTussenvoegsel(String tussenvoegsel) {
		this.tussenvoegsel = tussenvoegsel;
	}

}
