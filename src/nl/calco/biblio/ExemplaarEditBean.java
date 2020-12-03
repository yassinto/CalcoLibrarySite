package nl.calco.biblio;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

@ManagedBean(name = "exemplaarEditBean")
@ViewScoped

public class ExemplaarEditBean implements Serializable { // Bean for page that modifies books or copies

	// Attributes
	private Boek boek; 						// Book that needs to be possibly modified or that the copy is from 
	private Exemplaar exemplaar; 			// Copy that needs to be possibly modified
	private List<Categorie> categorieen; 	// List of categories
	private boolean correctIsbn; 			// Verifies if inserted ISBN is correct
	private boolean isAlleBoeken; 			// Verifies if the modification concerns book (so all) or just a copy
	private boolean isSession = false; 		// Makes sure the previous boolean is established once


	
	public Boek getBoek() { // Get book from previous page
		if (boek == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			boek = (Boek) sessionMap.get("boek"); 
			sessionMap.remove("boek");
		}
		return boek;
	}

	public Exemplaar getExemplaar() { // Gets copy from previous page 
		if (exemplaar == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			exemplaar = (Exemplaar) sessionMap.get("exemplaar");
			sessionMap.remove("exemplaar");
		}
		return exemplaar;
	}

	public List<Categorie> getCategorieen() { // Gets categories from database
		if (categorieen == null) {
			Database database = new Database();
			try {
				categorieen = database.getCategorieen();
			} catch (SQLException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Fout bij het ophalen van categorieën"));
			} catch (NamingException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Fout bij het verbinden met de database"));
			}
		}
		return categorieen;
	}

	public String opslaan() {  // Action: save book or copy modification
		String opslaan = null;
		Database database = new Database();
		FacesContext context = FacesContext.getCurrentInstance();
		String filter = "";
		String cijfers = "";
		boolean incorrectBoek = false; // Boolean to verify correct insertions 

		if (boek.getIsbn() != null) {
			filter = boek.getIsbn().replaceAll("[\\s\\-\\_]", ""); // removes allowed characters from inserted ISBN
			cijfers = boek.getIsbn().replaceAll("[\\D]", ""); // removes all non-digit characters
		}
		if (!(cijfers.length() == filter.length())) { // checks for illegal characters inserted ISBN
			context.addMessage(null,
					new FacesMessage("ISBN bevat ongeldige tekens. Gebruik alleen cijfers, spaties, '-' of '_'"));
			incorrectBoek = true;
		}
		if ((cijfers.length() == 10) || !(cijfers.length() == 13)) { // checks for correct length ISBN
			context.addMessage(null,
					new FacesMessage("ISBN lengte is ongeldig. Voer een 10-ISBN of een 13-ISBN in" + cijfers));
			incorrectBoek = true;
		}
		if ((boek.getCategorie() == null)) {
			context.addMessage(null, new FacesMessage("Categorie selecteren is verplicht"));
			incorrectBoek = true;
		}
		if (incorrectBoek == false) {
			try {
				if (cijfers.length() == 10) {
					controleerIsbn10(cijfers);
				}
				if (cijfers.length() == 13) {
					controleerIsbn13(cijfers);
				}
				if (correctIsbn) { // when all is right book or copy is updated in database
					if (isAlleBoeken) {
						boek.setIsbn(cijfers);
						database.updateBoek(boek);
					} else {
						database.updateExemplaar(exemplaar);
					}
					Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
					sessionMap.put("boek", boek); // Puts book back to sessionMap before returning to previous page
					opslaan = "boekUitIn";
				}
			} catch (SQLException ex) {
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null,
						new FacesMessage("Kan boek niet toegevoegd worden, helaas: " + ex.getMessage()));
			} catch (NamingException ex) {
				FacesContext context1 = FacesContext.getCurrentInstance();
				context1.addMessage(null, new FacesMessage("De database kan niet worden benaderd"));
			}
		}
		return opslaan;
	}

	public String annuleren() { // Action: cancel and return
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", boek); // Puts book back to sessionMap before returning to previous page
		return "boekUitIn";
	}

	private void controleerIsbn10(String cijfers) { // Method to check if inserted 10-digit ISBN is correct 
		int som = 0;
		int factor = 10;
		for (char c : cijfers.toCharArray()) {
			som += (c - '0') * factor--;
		}
		if (som % 11 != 0) {
			correctIsbn = false;
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Dit ISBN-10 is niet correct"));
		} else {
			correctIsbn = true;
		}
	}

	private void controleerIsbn13(String cijfers) { // Method to check if inserted 13-digit ISBN is correct 
		int som = 0;
		int factor = 1;
		for (char c : cijfers.toCharArray()) {
			som += (c - '0') * factor;
			factor = 4 - factor;
		}
		if (som % 10 != 0) {
			correctIsbn = false;
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Dit ISBN-13 is niet correct"));
		} else {
			correctIsbn = true;
		}
	}
	
	public boolean isAlleBoeken() { // Verifies if it's all books or just a copy from previous page
		if (isSession == false) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			isAlleBoeken = (Boolean) sessionMap.get("alle");
			sessionMap.remove("alle");
			isSession = true;
		}
		return isAlleBoeken;
	}
	
	public Categorie getCategorie(Integer id) {  // Gets a category based on unique key - needed for converter of listItems categories
		Categorie categorie = null;
		if (id == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Kies een categorie"));
		} else {
			for (Categorie cat : categorieen) {
				if (id.equals(cat.getCategorieId())) {
					categorie = cat;
				}
			}
		}
		return categorie;
	}

}
