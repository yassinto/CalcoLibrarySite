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

@ManagedBean(name = "uitlenenBean")
@ViewScoped
public class UitlenenBean implements Serializable { // Bean for the lend-out page

	// Attributes
	private Boek boek; 					// Book to be lend out
	private Exemplaar exemplaar; 				// Copy of book to be lend out
	private List<Medewerker> medewerkers; 		// List of employees
	private Medewerker geselecteerdeMedewerker; // Selected employee

	public Boek getBoek() { // Gets book from previous page
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

	public List<Medewerker> getMedewerkers() { // Gets all the employees from
												// the database
		if (medewerkers == null) {
			Database database = new Database();
			try {
				medewerkers = database.getMedewerkers();
			} catch (SQLException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Fout bij het ophalen van categorieën"));
			} catch (NamingException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Fout bij het verbinden met de database"));
			}
		}
		return medewerkers;
	}

	// Getter and Setter for selected employee
	public Medewerker getGeselecteerdeMedewerker() {
		return geselecteerdeMedewerker;
	}

	public void setGeselecteerdeMedewerker(Medewerker geselecteerdeMedewerker) {
		this.geselecteerdeMedewerker = geselecteerdeMedewerker;
	}

	public String uitlenen() { // Action: lend out book copy to selected
								// employee
		String uitlenen = null;
		if (geselecteerdeMedewerker == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Selecteer medewerker"));
		} else {
			try {
				Database database = new Database();
				database.leenUit(exemplaar, geselecteerdeMedewerker);
				boek.setExemplaren(database.getExemplaren(boek.getBoekId())); // Refreshes book object
				uitlenen = "boekUitIn";
			} catch (SQLException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Fout bij lenen van het boek: " + ex.getMessage()));
			} catch (NamingException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Fout bij het verbinden met de database"));
			}
		}
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", boek);
		return uitlenen;
	}

	public String annuleren() { // Action: cancel and return
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("boek", boek);
		return "boekUitIn";
	}

}
