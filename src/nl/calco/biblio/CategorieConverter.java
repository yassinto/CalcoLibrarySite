package nl.calco.biblio;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "categorieConverter")
public class CategorieConverter implements Converter { // Converter class for converting listItems to category objects

	// Implementing abstract methods
	public Object getAsObject(FacesContext context, UIComponent component, String catId) {
		ValueExpression waarde = component.getValueExpression("value");
		String el = waarde.getExpressionString();
		el = getBeanEL(el);
		ValueExpression valex = context.getApplication().getExpressionFactory()
				.createValueExpression(context.getELContext(), el, Object.class);
		Object bean = (Object) valex.getValue(context.getELContext());
		if (bean instanceof BoekEditBean) {
			return ((BoekEditBean) bean).getCategorie(Integer.valueOf(catId));
		} else if (bean instanceof ExemplaarEditBean) {
			return ((ExemplaarEditBean) bean).getCategorie(Integer.valueOf(catId));
		} else {
			return null;
		}
	}

	public String getAsString(FacesContext context, UIComponent component, Object cat) {
		if (cat != null) {
			Categorie categorie = (Categorie) cat;
			return categorie.getCategorieId().toString();
		} else {
			return null;
		}
	}

	public String getBeanEL(String el) { // Get beanname needed to use class for different beans
		el = el.substring(0, el.indexOf('.'));
		el = el + "}";
		return el;
	}

}
