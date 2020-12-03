package nl.calco.biblio;

import java.io.Serializable;
import java.sql.BatchUpdateException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

@ManagedBean(name = "boekUitInBean")
@ViewScoped
public class BoekUitInBean implements Serializable { // Bean for Book loan/return page

	// Attributes
	private Boek boek; 							// Book selected in previous page
	private Exemplaar geselecteerdeExemplaar; 	// Selected copy

	public Boek getBoek() { // Gets book from sessionMap put there in previous page
		if (boek == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			boek = (Boek) sessionMap.get("boek");
			sessionMap.remove("boek");
		}
		return boek;
	}

	// Getter and Setter for selected copy
	public Exemplaar getGeselecteerdeExemplaar() {
		return geselecteerdeExemplaar;
	}

	public void setGeselecteerdeExemplaar(Exemplaar geselecteerdeExemplaar) {
		this.geselecteerdeExemplaar = geselecteerdeExemplaar;
	}

	public String wijzigen() { // Action: takes user to page to modify book
		boolean isAlleBoeken = true;
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", boek);
		sessionMap.put("alle", isAlleBoeken);
		return "wijzigen";
	}

	public String annuleren() { // Action: cancel and return
		return "boek";
	}

	public void innemen() { // Action: return selected copy
		try {
			Database database = new Database();
			database.neemIn(geselecteerdeExemplaar.getHuidigeUitlening());
			boek.setExemplaren(database.getExemplaren(boek.getBoekId()));
		} catch (SQLException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Fout bij het innemen van het boek: " + ex.getMessage()));
		} catch (NamingException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Fout bij het verbinden met de database"));
		}
	}

	public String uitlenen() { // Action: takes user to loan page
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", boek);
		sessionMap.put("exemplaar", geselecteerdeExemplaar);
		return "uitlenen";
	}

	public String bewerken() { // Action: takes user to page to modify copy
		boolean isAlleBoeken = false;
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", boek);
		sessionMap.put("exemplaar", geselecteerdeExemplaar);
		sessionMap.put("alle", isAlleBoeken);
		return "wijzigen";
	}

	public void verwijder() { // Action: deletes selected copy from database
		Database database = new Database();
		try {
			database.verwijderExemplaar(getGeselecteerdeExemplaar());
			for (Boek b : database.getBoeken("")) {
				if (b.getBoekId() == boek.getBoekId()) { // Updates book object
					boek = b;
				}
			}
		} catch (SQLDataException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Deze exemplaar kan niet worden verwijderd"));
		} catch (SQLException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Fout bij verwijderen van exemplaar:"));
		} catch (NamingException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("De database kan niet worden benaderd"));
		}
	}

	public String exemplaar() { // Action: takes user to page to add copies
		Boolean uit = true;
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", boek);
		sessionMap.put("uit", uit);
		return "exemplaar";
	}

}
