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

@ManagedBean(name = "boekEditBean")
@ViewScoped
public class BoekEditBean implements Serializable { // Bean for bookEdit page

	// Attributes
	private Boek boek; 						// New book to be inserted in database
	private List<Categorie> categorieen; 	// List of categories to be chosen for new book
	private boolean correctIsbn; 			// Boolean for verifying correct format of inserted ISBN number
	private int aantal; 					// Amount of copies to inserted alongside new book

	public Boek getBoek() { // Book getter
		if (boek == null) {
			try {
				boek = new Boek();
				Database database = new Database();
				boek.setBoekNummer(database.getNextBoekNummer()); 	// Gets next booknumber from database
				boek.setLocatie("Bieb 1, 3.10"); 					// Standard location for book (can be changed if desired)
			} catch (SQLException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(ex.getMessage()));
			} catch (NamingException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("De database kan niet worden benaderd"));
			}
		}
		return boek;
	}

	public List<Categorie> getCategorieen() { // Category list getter from database
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

	public Categorie getCategorie(Integer id) { // Gets a category based on unique key - needed for converter of listitems categories
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
	
	// Getter and Setter for amount of copies
	public int getAantal() {
		return aantal;
	}

	public void setAantal(int aantal) {
		this.aantal = aantal;
	}

	public String opslaan() { // Action: save new book
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
		if (aantal < 0) {
			context.addMessage(null, new FacesMessage("Examplaar aantal mag geen negatief getal zijn"));
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
				if (correctIsbn) {
					boek.setIsbn(cijfers);
					database.insertBoek(boek, aantal); // when all is right book and copies are inserted into database
					opslaan = "boek";
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

	public String annuleren() { // Action: cancel and return to previous page
		return "boek";
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

}
