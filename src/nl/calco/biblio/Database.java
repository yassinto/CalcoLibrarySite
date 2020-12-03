package nl.calco.biblio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Database { // Class for communicating with the database
	public Connection getConnection() throws SQLException, NamingException {
		Context context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup("jdbc/Bibliotheek");
		Connection connection = dataSource.getConnection();
		return connection;
	}

	public List<Categorie> getCategorieen() throws SQLException, NamingException { // Gets all categories
		List<Categorie> result = new ArrayList<Categorie>();
		String sql = "select * from categorien order by omschrijving";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Categorie categorie = new Categorie();
				categorie.setCategorieId(resultSet.getInt("categorie_id"));
				categorie.setOmschrijving(resultSet.getString("omschrijving"));
				result.add(categorie);
			}
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		return result;
	}

	public List<Boek> getBoeken(String filt) throws SQLException, NamingException { // Gets all book potentially filtered
		List<Boek> result = new ArrayList<Boek>();
		String filter = "%" + (filt == null ? "" : filt) + "%";
		String sql = "select * from boeken where boeknummer like ? or titel like ? or auteur like ? or isbn like ? order by boeknummer";
		String sqlCategorie = "select * from categorien where categorie_id = ?";
		int categorieId = 0;
		Connection connection = null;
		Connection connectionCat = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		PreparedStatement preparedStatementCat = null;
		ResultSet resultSetCat = null;
		try {
			connection = getConnection();
			connectionCat = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, filter);
			preparedStatement.setString(2, filter);
			preparedStatement.setString(3, filter);
			preparedStatement.setString(4, filter);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Boek boek = new Boek();
				boek.setBoekNummer(resultSet.getString("BoekNummer"));
				boek.setTitel(resultSet.getString("Titel"));
				boek.setAuteur(resultSet.getString("Auteur"));
				boek.setIsbn(resultSet.getString("ISBN"));
				boek.setLocatie(resultSet.getString("Locatie"));
				boek.setUitgeverij(resultSet.getString("Uitgeverij"));
				boek.setBoekId(resultSet.getInt("Boek_ID"));
				boek.setExemplaren(getExemplaren(resultSet.getInt("Boek_ID")));

				Categorie categorie = new Categorie();
				preparedStatementCat = connectionCat.prepareStatement(sqlCategorie);
				categorie.setCategorieId(resultSet.getInt("Categorie_ID"));
				categorieId = resultSet.getInt("Categorie_ID");
				preparedStatementCat.setInt(1, categorieId);
				resultSetCat = preparedStatementCat.executeQuery();
				while (resultSetCat.next()) {
					categorie.setOmschrijving(resultSetCat.getString("Omschrijving"));
				}
				boek.setCategorie(categorie);
				result.add(boek);
			}
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (resultSetCat != null && !resultSetCat.isClosed()) {
				resultSetCat.close();
			}
			if (preparedStatementCat != null && !preparedStatementCat.isClosed()) {
				preparedStatementCat.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
			if (connectionCat != null && !connectionCat.isClosed()) {
				connectionCat.close();
			}

		}
		return result;
	}

	public List<Exemplaar> getExemplaren(int boekId) throws SQLException, NamingException { // Get all copies from certain book
		List<Exemplaar> result = new ArrayList<Exemplaar>();
		String sql = "select * from exemplaren where boek_id = ? order by exemplaarvolgnummer";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, boekId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Exemplaar exemplaar = new Exemplaar();
				exemplaar.setBoekId(resultSet.getInt("boek_id"));
				exemplaar.setExemplaarId(resultSet.getInt("exemplaar_id"));
				exemplaar.setExemplaarVolg(resultSet.getInt("exemplaarvolgnummer"));
				exemplaar.setVermist(resultSet.getBoolean("vermist"));
				exemplaar.setHuidigeUitlening(getUitlening(resultSet.getInt("exemplaar_id")));
				result.add(exemplaar);
			}
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		return result;
	}

	public List<Medewerker> getMedewerkers() throws SQLException, NamingException { // Gets all employees
		List<Medewerker> result = new ArrayList<Medewerker>();
		String sql = "select * from medewerkers order by achternaam";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Medewerker medewerker = new Medewerker();
				String voornaam = resultSet.getString("voornaam");
				String achternaam = resultSet.getString("achternaam");
				String naam = "";
				if (resultSet.getString("tussenvoegsel") != null) {
					String tussenvoegsel = resultSet.getString("tussenvoegsel");
					naam = achternaam.trim() + ", " + voornaam.trim() + " " + tussenvoegsel.trim();
					medewerker.setTussenvoegsel(tussenvoegsel);
				} else {
					naam = achternaam.trim() + ", " + voornaam.trim();
				}
				medewerker.setMedewerkerId(resultSet.getInt("medewerker_id"));
				medewerker.setAchternaam(achternaam);
				medewerker.setVoornaam(voornaam);
				medewerker.setNaam(naam);
				medewerker.setEmail(resultSet.getString("email"));
				medewerker.setBoeken(getBoekBezit(medewerker));
				result.add(medewerker);
			}
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		return result;
	}

	public List<BoekBezit> getBoekBezit(Medewerker medewerker) throws SQLException, NamingException { // Gets all books in possession of an employee
		List<BoekBezit> result = new ArrayList<BoekBezit>();
		String sql = "select * from uitleningen where medewerker_id = ? and datuminleveren is NULL";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, medewerker.getMedewerkerId());
			resultSet = preparedStatement.executeQuery();
			List<Boek> boeken = getBoeken("");
			while (resultSet.next()) {
				BoekBezit boekBezit = new BoekBezit();
				boekBezit.setDatumUitleen(resultSet.getDate("datumuitleen"));
				boekBezit.setExemplaarId(resultSet.getInt("exemplaar_id"));
				for (Boek boek : boeken) {
					for (Exemplaar exemplaar : boek.getExemplaren()) {
						if (exemplaar.getExemplaarId() == boekBezit.getExemplaarId())
							boekBezit.setTitel(boek.getTitel());
						boekBezit.setBoekNummer(boek.getBoekNummer());
						boekBezit.setBoekVolg(exemplaar.getExemplaarVolg());
					}
				}
				result.add(boekBezit);
			}
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		return result;
	}

	public Uitlening getUitlening(int exemplaarId) throws SQLException, NamingException { // Gets current lending of certain copy
		Uitlening result = new Uitlening();
		String sql = "select * from uitleningen where exemplaar_id = ? and datuminleveren is null";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, exemplaarId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				result.setDatumUitleen(resultSet.getDate("datumuitleen"));
				result.setMedewerker(getMedewerker(resultSet.getInt("medewerker_id")));
				result.setExemplaarId(resultSet.getInt("exemplaar_id"));
			}
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		return result;
	}

	public Medewerker getMedewerker(int medewerkerId) throws SQLException, NamingException { // Get employee from employee ID
		Medewerker result = new Medewerker();
		String sql = "select * from medewerkers where medewerker_id = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String naam = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, medewerkerId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String voornaam = resultSet.getString("voornaam");
				String achternaam = resultSet.getString("achternaam");
				if (resultSet.getString("tussenvoegsel") != null) {
					String tussenvoegsel = resultSet.getString("tussenvoegsel");
					naam = achternaam.trim() + ", " + voornaam.trim() + " " + tussenvoegsel.trim();
					result.setTussenvoegsel(tussenvoegsel);
				} else {
					naam = achternaam.trim() + ", " + voornaam.trim();
				}
				result.setMedewerkerId(medewerkerId);
				result.setAchternaam(achternaam);
				result.setVoornaam(voornaam);
				result.setNaam(naam);
				result.setEmail(resultSet.getString("email"));
			}
		} finally {
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		return result;
	}

	public void insertCategorie(Categorie categorie) throws NamingException, SQLException { // Inserts new category
		String sql = "insert into categorien (omschrijving) values (?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, categorie.getOmschrijving());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void updateCategorie(Categorie categorie) throws NamingException, SQLException { // Updates a current category
		String sql = "update categorien set omschrijving = ? where categorie_id = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, categorie.getOmschrijving());
			preparedStatement.setInt(2, categorie.getCategorieId());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void verwijderCategorie(Categorie categorie) throws NamingException, SQLException { // Removes a category
		String sql = "delete from categorien where categorie_id = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, categorie.getCategorieId());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void insertBoek(Boek boek, int aantal) throws NamingException, SQLException { // Inserts a new book
		String sql = "insert into boeken (boeknummer, titel, auteur, uitgeverij, isbn, locatie, categorie_id) values (?, ?, ?, ?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, boek.getBoekNummer());
			preparedStatement.setString(2, boek.getTitel());
			preparedStatement.setString(3, boek.getAuteur());
			preparedStatement.setString(4, boek.getUitgeverij());
			preparedStatement.setString(5, boek.getIsbn());
			preparedStatement.setString(6, boek.getLocatie());
			preparedStatement.setInt(7, boek.getCategorie().getCategorieId());
			preparedStatement.execute();
			resultSet = preparedStatement.getGeneratedKeys();
			while (resultSet.next()) {
				boek.setBoekId(resultSet.getInt(1));
			}
			if (aantal > 0) {
				insertExamplaren(boek, 1, aantal);
			}
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
			if (resultSet != null && !resultSet.isClosed()) {
				resultSet.close();
			}
		}
	}

	public void updateBoek(Boek boek) throws NamingException, SQLException { // Updates a book
		String sql = "update boeken set titel = ?, auteur = ?, uitgeverij = ?, isbn = ?, locatie = ?, categorie_id = ? where boek_id = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, boek.getTitel());
			preparedStatement.setString(2, boek.getAuteur());
			preparedStatement.setString(3, boek.getUitgeverij());
			preparedStatement.setString(4, boek.getIsbn());
			preparedStatement.setString(5, boek.getLocatie());
			preparedStatement.setInt(6, boek.getCategorie().getCategorieId());
			preparedStatement.setInt(7, boek.getBoekId());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void insertExamplaren(Boek boek, int start, int totaal) throws NamingException, SQLException { // Inserts copies for a book
		String sql = "insert into exemplaren (boek_id, exemplaarvolgnummer, datumaanschaf) values (?, ?, ?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			for (; start <= totaal; start++) {
				preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setInt(1, boek.getBoekId());
				preparedStatement.setInt(2, start);
				preparedStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
				preparedStatement.execute();
			}
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void updateExemplaar(Exemplaar exemplaar) throws NamingException, SQLException { // Updates a copy
		String sql = "update exemplaren set vermist = ? where exemplaar_id = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			if (exemplaar.isVermist()) {
				preparedStatement.setBoolean(1, true);
			} else {
				preparedStatement.setNull(1, Types.BIT);
			}
			preparedStatement.setInt(2, exemplaar.getExemplaarId());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void verwijderExemplaar(Exemplaar exemplaar) throws NamingException, SQLException { // Removes a copy
		String sql = "delete from exemplaren where exemplaar_id = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, exemplaar.getExemplaarId());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void insertMedewerker(Medewerker medewerker) throws NamingException, SQLException { // Inserts a new employee
		String sql = "insert into medewerkers (voornaam, tussenvoegsel, achternaam, email) values (?, ?, ?, ?)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, medewerker.getVoornaam());
			if ((medewerker.getTussenvoegsel() == null) || (medewerker.getTussenvoegsel().trim().equals(""))) {
				preparedStatement.setNull(2, Types.VARCHAR);
			} else {
				preparedStatement.setString(2, medewerker.getTussenvoegsel());
			}
			preparedStatement.setString(3, medewerker.getAchternaam());
			preparedStatement.setString(4, medewerker.getEmail());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void updateMedewerker(Medewerker medewerker) throws NamingException, SQLException { // Updates a current employee
		String sql = "update medewerkers set voornaam = ?, tussenvoegsel = ?, achternaam = ?, email = ? where medewerker_id = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, medewerker.getVoornaam());
			if ((medewerker.getTussenvoegsel() == null) || (medewerker.getTussenvoegsel().trim().equals(""))) {
				preparedStatement.setNull(2, Types.VARCHAR);
			} else {
				preparedStatement.setString(2, medewerker.getTussenvoegsel());
			}
			preparedStatement.setString(3, medewerker.getAchternaam());
			preparedStatement.setString(4, medewerker.getEmail());
			preparedStatement.setInt(5, medewerker.getMedewerkerId());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public String getNextBoekNummer() throws NamingException, SQLException { // Method to get next booknumber
		String boekNummer = null;
		String sql = "select max(BoekNummer) as BoekNummer from boeken";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				int nummer = Integer.parseInt(resultSet.getString("BoekNummer").substring(2).trim());
				boekNummer = "CA" + ++nummer;
			}
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		return boekNummer;
	}

	public void leenUit(Exemplaar exemplaar, Medewerker medewerker) throws NamingException, SQLException { // Loans a copy to an employee
		String sql = "insert into uitleningen (exemplaar_id, medewerker_id, datumuitleen, datuminleveren) values (?, ?, ?, NULL)";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, exemplaar.getExemplaarId());
			preparedStatement.setInt(2, medewerker.getMedewerkerId());
			preparedStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			preparedStatement.execute();

		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void neemIn(Uitlening uitlening) throws NamingException, SQLException { // Returns a copy from a lending
		String sql = "update uitleningen set datuminleveren = ? where exemplaar_id = ? and medewerker_id = ? and datuminleveren is NULL";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
			preparedStatement.setInt(2, uitlening.getExemplaarId());
			preparedStatement.setInt(3, uitlening.getMedewerker().getMedewerkerId());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void neemIn(Medewerker medewerker, BoekBezit boekBezit) throws NamingException, SQLException { // Returns a copy from an employee
		String sql = "update uitleningen set datuminleveren = ? where exemplaar_id = ? and medewerker_id = ? and datuminleveren is NULL";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
			preparedStatement.setInt(2, boekBezit.getExemplaarId());
			preparedStatement.setInt(3, medewerker.getMedewerkerId());
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	public void draaiTerug(Medewerker medewerker, BoekBezit boekBezit) throws NamingException, SQLException { // Reserves a returning
		String sql = "update uitleningen set datuminleveren = NULL where exemplaar_id = ? and medewerker_id = ? and datuminleveren = ?";
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = getConnection();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, boekBezit.getExemplaarId());
			preparedStatement.setInt(2, medewerker.getMedewerkerId());
			preparedStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			preparedStatement.execute();
		} finally {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

}
