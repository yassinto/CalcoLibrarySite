package nl.calco.biblio;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

@ManagedBean(name = "medewerkersBean")
@ViewScoped
public class MedewerkersBean implements Serializable { // Bean for employee page

	// Attributes
	private List<Medewerker> medewerkers; 		// List of all employees
	private Medewerker geselecteerdeMedewerker; // Selected employee

	public List<Medewerker> getMedewerkers() { // Gets all employees from database
		if (medewerkers == null) {
			Database database = new Database();
			try {
				medewerkers = database.getMedewerkers();
			} catch (SQLException ex) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Fout bij het ophalen van medewerkers"));
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

	// Action: takes user to books in possession page
	public String toonBoeken() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("medewerker", geselecteerdeMedewerker);
		return "toon";
	}

	// Action: takes user to edit employee page
	public String wijzigen() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("medewerker", geselecteerdeMedewerker);
		return "edit";

	}

	// Action: takes user to page to add new employee
	public String toevoegen() {
		geselecteerdeMedewerker = new Medewerker();
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("medewerker", geselecteerdeMedewerker);
		return "edit";

	}

	// Action: return
	public String terug() {
		return "boeken";
	}

}
