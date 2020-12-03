package nl.calco.biblio;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

@ManagedBean(name = "exemplaarBean")
@ViewScoped
public class ExemplaarBean implements Serializable { // Bean for page for adding copies

	// Attributes
	private Boek boek; 					// Book that the user wants to had copies too
	private int aantal; 				// Amount of copies added
	private boolean terugUit; 			// Verifies if it should return to the Book out/in page
	private boolean isSession = false; 	// Makes sure the previous boolean is established once

	public Boek getBoek() { // Gets book from previously selected page
		if (boek == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			boek = (Boek) sessionMap.get("boek");
			sessionMap.remove("boek");
		}
		return boek;
	}
	
	// Getter and Setter for amount
	public int getAantal() {
		return aantal;
	}

	public void setAantal(int aantal) {
		this.aantal = aantal;
	}

	public String opslaan() { // Action: saves new amount
		String opslaan = null;
		FacesContext context = FacesContext.getCurrentInstance();
		isTerugUit();

		if (aantal < 0) {
			context.addMessage(null, new FacesMessage("Aantal mag niet negatief zijn"));
		} else {
			int start = boek.getAantalExemplaren() + 1;
			int totaal = aantal + boek.getAantalExemplaren();
			Database database = new Database();
			try {
				database.insertExamplaren(boek, start, totaal);
				if (terugUit == true) {
					opslaan = "uit";
					boek.setExemplaren(database.getExemplaren(boek.getBoekId())); // Refreshes book object
					Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
					sessionMap.put("boek", boek);
				} else {
					opslaan = "boek";
				}
			} catch (SQLException ex) {
				context.addMessage(null, new FacesMessage(ex.getMessage()));
			} catch (NamingException ex) {
				context.addMessage(null, new FacesMessage("Fout bij het verbinden met de database"));
			}
		}
		return opslaan;
	}

	public String annuleren() { // Action: cancel and return
		isTerugUit();
		if (terugUit == true) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			sessionMap.put("boek", boek);
			return "uit";
		}
		return "boek";
	}

	public boolean isTerugUit() { // Method: verifies if the previous page was the book out/in page
		if (isSession == false) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			terugUit = (Boolean) sessionMap.get("uit");
			sessionMap.remove("uit");
			isSession = true;
		}
		return terugUit;
	}

}
