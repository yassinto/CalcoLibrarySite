package nl.calco.biblio;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

@ManagedBean(name = "toonBoekenBean")
@ViewScoped
public class ToonBoekenBean implements Serializable { // Bean for book possession page

	// Attributes
	private Medewerker medewerker; 			// Employee whom the books are in possession of
	private String gebruikersNaam; 			// Username
	private BoekBezit geselecteerdeBoek; 	// Selected book

	public Medewerker getMedewerker() { // Get employee from previous page selected
		if (medewerker == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			medewerker = (Medewerker) sessionMap.get("medewerker");
			sessionMap.remove("medewerker");
		}
		return medewerker;
	}

	public String getGebruikersNaam() { // Makes a username from email
		if (gebruikersNaam == null) {
			gebruikersNaam = getMedewerker().getEmail().substring(0, getMedewerker().getEmail().indexOf('@'));
		}
		return gebruikersNaam;
	}

	// Getter and Setter from selected book
	public BoekBezit getGeselecteerdeBoek() {
		return geselecteerdeBoek;
	}

	public void setGeselecteerdeBoek(BoekBezit geselecteerdeBoek) {
		this.geselecteerdeBoek = geselecteerdeBoek;
	}

	public void innemen() { // Action: return book
		geselecteerdeBoek.setDatumInleveren(new java.sql.Date(System.currentTimeMillis()));
		try {
			Database database = new Database();
			database.neemIn(medewerker, geselecteerdeBoek);
		} catch (SQLException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Fout bij het innemen van het boek: " + ex.getMessage()));
		} catch (NamingException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Fout bij het verbinden met de database"));
		}
	}

	public void terugdraaien() { // Action: revert returning of book
		geselecteerdeBoek.setDatumInleveren(null);
		try {
			Database database = new Database();
			database.draaiTerug(medewerker, geselecteerdeBoek);
		} catch (SQLException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Fout bij het terugdraaien van inname: " + ex.getMessage()));
		} catch (NamingException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Fout bij het verbinden met de database"));
		}

	}

	public String terug() { // Action: return
		return "medewerkers";
	}

}
