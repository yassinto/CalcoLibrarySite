package nl.calco.biblio;

import java.io.Serializable;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "categorieenBean")
@ViewScoped

public class CategorieenBean implements Serializable { // Bean for category page

	// Attributes
	private List<Categorie> categorieen; 		// List of categories as found in database
	private Categorie geselecteerdeCategorie; 	// Selected category

	public List<Categorie> getCategorieen() { // Get categories from database
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
	
	// Getter and Setter for selected category
		public Categorie getGeselecteerdeCategorie() {
			return geselecteerdeCategorie;
		}

		public void setGeselecteerdeCategorie(Categorie categorie) {
			geselecteerdeCategorie = categorie;
		}

	public void verwijderCategorie() { // Action: delete selected category
		Database database = new Database();
		try {
			database.verwijderCategorie(getGeselecteerdeCategorie());
		} catch (BatchUpdateException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Deze categorie kan niet worden verwijderd"));
		} catch (SQLException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Selectie kan niet worden verwijderd"));
		} catch (NamingException ex) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("De database kan niet worden benaderd"));
		} finally {
			categorieen = null;
		}
	}

	public String wijzigen() { // Action: takes user to page to edit selected category
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("categorie", geselecteerdeCategorie); 
		return "edit";
	}

	public String nieuw() { // Action: takes user to page to insert new category
		geselecteerdeCategorie = new Categorie();
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
		sessionMap.put("categorie", geselecteerdeCategorie); 
		return "edit";
	}

	public String terug() { // Action: return
		return "boek";
	}

}
