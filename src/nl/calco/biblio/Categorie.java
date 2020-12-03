package nl.calco.biblio;

import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;

public class Categorie implements Serializable { // Category class

	// Attributes as found in database
	private Integer categorieId; // Unique key
	private String omschrijving; // Description

	// Getters & Setters
	public Integer getCategorieId() {
		return categorieId;
	}

	public void setCategorieId(Integer categorieId) {
		this.categorieId = categorieId;
	}

	public String getOmschrijving() {
		return omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving.trim();
	}

	public String toString() { // Override: description as string representation
								// of object
		return omschrijving;
	}

	// Override equals and hashCode methods to matches objects in itemLists to equal object
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if ((obj instanceof Categorie)) {
			Categorie cat = (Categorie) obj;
			if (cat.categorieId.equals(this.categorieId)) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Objects.hashCode(this.categorieId);
		return hash;
	}

}
