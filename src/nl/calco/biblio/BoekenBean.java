package nl.calco.biblio;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@ManagedBean(name = "boekenBean")
@ViewScoped

public class BoekenBean implements Serializable { // Bean for books page

	// Attributes
	private List<Boek> boeken; 		// List of books to be displayed on page
	private String filter; 			// Filter keyword for searching books
	private Boek geselecteerdeBoek; // Selected book

	public List<Boek> getBoeken() { // Gets books from database
		Database database = new Database();
		try {
			boeken = database.getBoeken(filter);
		} catch (SQLException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(ex.getMessage()));
		} catch (NamingException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Fout bij het verbinden met de database"));
		}
		return boeken;
	}
	
	// Getters & Setters
		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public Boek getGeselecteerdeBoek() {
			return geselecteerdeBoek;
		}

		public void setGeselecteerdeBoek(Boek geselecteerdeBoek) {
			this.geselecteerdeBoek = geselecteerdeBoek;
		}

	public void zoek() { // Action: search books by getting books again with possible filtering keyword
		getBoeken();
	}

	public void clear() { // Action: clear all searchresults
		boeken = null;
		filter = null;
		geselecteerdeBoek = null;
		getBoeken();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Zoekresultaat is gereset"));
	}

	public String lenen() { // Action: takes user to Books loaning/returning page
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", geselecteerdeBoek); // Puts selected book in sessionMap to use in next page
		return "lenen";
	}

	public String exemplaarToevoegen() { // Action: takes user to Add copies page
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", geselecteerdeBoek); // Puts selected book in sessionMap to use in next page
		return "exemplaar";
	}

	public String titelToevoegen() { 	// Action: takes user to add book page
		return "titel";
	}

	public String categorie() { 		// Action: takes user to categories page
		return "categorie";
	}

	public void overzicht() { // Placeholder for future feature
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Funtie nog niet in gebruik"));
	}

	public String medewerkers() { // Action: takes user to employees page
		return "medewerkers";

	}

}
