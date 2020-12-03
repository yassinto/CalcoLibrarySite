package nl.calco.biblio;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

@ManagedBean(name = "categorieenEditBean")
@ViewScoped

public class CategorieEditBean implements Serializable { // Bean for category edit page

	// Attributes
	private Categorie categorie; // Category object

	public Categorie getCategorie() { // Get category object from sessionMap
		if (categorie == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
			categorie = (Categorie) sessionMap.get("categorie");
			sessionMap.remove("categorie");
		}
		return categorie;
	}

	public String opslaan() { // Action: save new category of new adjustments to existing category
		String opslaan = null;
		Database database = new Database();
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			if (categorie.getCategorieId() == null) { 	// When no category ID is presents its a new category
				database.insertCategorie(categorie);
				opslaan = "terug";
			} else { 									// When there is an category ID its an existing category to be updated
				database.updateCategorie(categorie);
				opslaan = "terug";
			}
		} catch (SQLException ex) {
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage(ex.getMessage()));
		} catch (NamingException ex) {
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("De database kan niet worden benaderd"));
		}
		return opslaan;
	}

	public String annuleren() { // Action: cancel and return
		return "terug";
	}

}
