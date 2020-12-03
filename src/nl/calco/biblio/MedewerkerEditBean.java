package nl.calco.biblio;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;

@ManagedBean(name = "medewerkersEditBean")
@ViewScoped
public class MedewerkerEditBean implements Serializable { // Bean for employee edit page

	private Medewerker medewerker; // Employee to be modified or inserted

	public Medewerker getMedewerker() { // Get employee from previous selected page
		if (medewerker == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			medewerker = (Medewerker) sessionMap.get("medewerker");
			sessionMap.remove("medewerker");
		}
		return medewerker;
	}

	public String opslaan() { // Action: save modifications or new employee
		String opslaan = null;
		FacesContext context = FacesContext.getCurrentInstance();
		Database database = new Database();
		boolean correcteInvoer = true; // Verifies if everything is inserted correctly
		if (checkNaam(medewerker.getAchternaam()) || checkNaam(medewerker.getVoornaam())
				|| checkNaam(medewerker.getTussenvoegsel())) {
			context.addMessage(null, new FacesMessage("Gebruik alleen geldige tekens"));
			correcteInvoer = false;
		}
		if (checkEmail(medewerker.getEmail())) {
			context.addMessage(null, new FacesMessage("Email formaat ongeldig"));
			correcteInvoer = false;
		}
		if (correcteInvoer) { // If all is correct updates or inserts into database
			try {
				if (medewerker.getMedewerkerId() == null) {
					database.insertMedewerker(medewerker);
					;
					opslaan = "medewerkers";
				} else {
					database.updateMedewerker(medewerker);
					opslaan = "medewerkers";
				}
			} catch (SQLException ex) {
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("Kan medewerker niet toevoegen of wijzigen"));
			} catch (NamingException ex) {
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("De database kan niet worden benaderd"));
			}
		}
		return opslaan;
	}

	public String annuleren() { // Action: cancel and return
		return "medewerkers";
	}
	
	public boolean checkNaam(String naam) { // Method: checks names for illegal characters
		naam = naam.replaceAll("[\\s\\-\\_\\.\\'a-zA-ZÀ-ÿ]", "");
		if (naam.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean checkEmail(String email) { // Method: check inserted email format
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			return true;
		}
		return false;
	}
}
