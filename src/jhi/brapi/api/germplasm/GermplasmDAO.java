package jhi.brapi.api.germplasm;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.*;
import java.util.*;

import jhi.brapi.api.*;
import jhi.brapi.util.*;

import org.restlet.*;

/**
 * Specifies the public interface which any ServerGermplasmSearch data accessing classes must implement.
 */
public class GermplasmDAO
{
	private static final SimpleDateFormat FORMAT_OUTPUT = new SimpleDateFormat("YYYYMMDD");

	private final String queryPartSynonyms = " ( SELECT GROUP_CONCAT( DISTINCT synonym ORDER BY synonym ) FROM synonyms LEFT JOIN synonymtypes ON synonyms.synonymtype_id = synonymtypes.id WHERE synonymtypes.target_table = 'germinatebase' AND synonyms.foreign_id = germinatebase.id ) AS synonyms ";

	private final String tables = " germinatebase LEFT JOIN locations ON germinatebase.location_id = locations.id LEFT JOIN countries ON locations.country_id = countries.id LEFT JOIN taxonomies ON germinatebase.taxonomy_id = taxonomies.id LEFT JOIN subtaxa ON subtaxa.id = germinatebase.subtaxa_id LEFT JOIN institutions ON germinatebase.institution_id = institutions.id LEFT JOIN pedigreedefinitions ON germinatebase.id = pedigreedefinitions.germinatebase_id ";

	private final String getLinesWhere = "SELECT SQL_CALC_FOUND_ROWS *, " + queryPartSynonyms + " FROM " + tables + "%s LIMIT ?, ?";

	// Simply selects all fields from germinatebase where the given id matches the id from the URI
	private final String getSpecificLine = "SELECT *, " + queryPartSynonyms + " FROM " + tables + " where germinatebase.id=?";

	private final String pedigreeByIdQuery = "SELECT p1.germinatebase_id, p1.parent_id as 'left_parent', p2.parent_id as 'right_parent', definition , name FROM pedigrees p1 INNER JOIN pedigrees p2 ON p1.germinatebase_id = p2.germinatebase_id and p1.pedigreedescription_id = 1 and p2.pedigreedescription_id = 2 JOIN germinatebase ON p1.germinatebase_id = germinatebase.id JOIN pedigreedefinitions ON p1.germinatebase_id WHERE p1.germinatebase_id = ?";

	// Used to get the genotpye data sets that are held in HDF5 files
	private final String genotypeDatasets = "SELECT datasets.id FROM datasets " +
		"LEFT JOIN experiments ON experiment_id = experiments.id WHERE " +
		"experiments.experiment_type_id = 1";

	public BrapiBaseResource<BrapiGermplasmMcpd> getById(String id)
	{
		BrapiBaseResource<BrapiGermplasmMcpd> germplasm = new BrapiBaseResource<>();

		try (Connection con = Database.INSTANCE.getDataSourceGerminate().getConnection();
			 PreparedStatement statement = DatabaseUtils.createByIdStatement(con, getSpecificLine, id);
			 ResultSet resultSet = statement.executeQuery())
		{
			if (resultSet.first())
			{
				// Set the ServerGermplasmSearch bean using the data returned from the database
				germplasm = new BrapiBaseResource<BrapiGermplasmMcpd>(getBrapiGermplasmMcpd(resultSet));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return germplasm;
	}

	public BrapiBaseResource<BrapiGermplasmMcpd> getMcpdFor(String id)
	{
		BrapiBaseResource<BrapiGermplasmMcpd> result = new BrapiBaseResource<>();

		try (Connection con = Database.INSTANCE.getDataSourceGerminate().getConnection();
			 PreparedStatement statement = DatabaseUtils.createByIdStatement(con, getSpecificLine, id);
			 ResultSet resultSet = statement.executeQuery())
		{
			if (resultSet.first())
				result = new BrapiBaseResource<BrapiGermplasmMcpd>(getBrapiGermplasmMcpd(resultSet));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return result;
	}

	private BrapiGermplasmMcpd getBrapiGermplasmMcpd(ResultSet resultSet) throws SQLException
	{
		BrapiGermplasmMcpd mcpd = new BrapiGermplasmMcpd();

		mcpd.setGermplasmDbId(resultSet.getString("germinatebase.id"));
		mcpd.setGermplasmPUI(resultSet.getString("germinatebase.general_identifier"));
		mcpd.setGermplasmName(resultSet.getString("germinatebase.name"));
		mcpd.setAccessionNumber(resultSet.getString("germinatebase.number"));
		mcpd.setSynonyms(getSynonyms(resultSet));
		mcpd.setCommonCropName(null); // TODO
		mcpd.setInstituteCode(resultSet.getString("institutions.code"));
		mcpd.setInstituteName(resultSet.getString("institutions.name"));
		mcpd.setBiologicalStatusOfAccessionCode(null); // TODO
		mcpd.setCountryOfOriginCode(resultSet.getString("countries.country_code3"));
		mcpd.setTypeOfGermplasmStorageCode(null); // TODO
		mcpd.setGenus(resultSet.getString("taxonomies.genus"));
		mcpd.setSpecies(resultSet.getString("taxonomies.species"));
		mcpd.setSpeciesAuthority(resultSet.getString("taxonomies.species_author"));
		mcpd.setSubtaxa(resultSet.getString("subtaxa.taxonomic_identifier"));
		mcpd.setSubtaxaAuthority(resultSet.getString("subtaxa.subtaxa_author"));
		mcpd.setPedigree(resultSet.getString("pedigreedefinitions.definition"));
		mcpd.setDefaultDisplayName(resultSet.getString("germinatebase.name"));
		mcpd.setSeedSource(null); // TODO

		String donorCode = resultSet.getString("germinatebase.donor_code");
		String donorNumber = resultSet.getString("germinatebase.donor_number");
		List<BrapiGermplasmDonor> donorList = new ArrayList<>();

		if ((donorCode != null && !donorCode.equals("")) || (donorNumber != null && !donorNumber.equals("")))
		{
			BrapiGermplasmDonor donor = new BrapiGermplasmDonor();
			donor.setDonorInstituteCode(donorCode);
			donor.setDonorAccessionNumber(donorNumber);
			donorList.add(donor);
		}

		mcpd.setDonors(donorList);
		Date acquisitionDate = resultSet.getDate("germinatebase.colldate");
		if (acquisitionDate != null)
			mcpd.setAcquisitionDate(FORMAT_OUTPUT.format(resultSet.getDate("germinatebase.colldate")));

		return mcpd;
	}

	private List<String> getDataSetIds()
	{
		List<String> dataSetIds = new ArrayList<>();

		try (Connection con = Database.INSTANCE.getDataSourceGerminate().getConnection();
			 PreparedStatement statement = con.prepareStatement(genotypeDatasets);
			 ResultSet resultSet = statement.executeQuery())
		{
			while (resultSet.next())
				dataSetIds.add(resultSet.getString("id"));
		}
		catch (SQLException e) {e.printStackTrace(); }

		return dataSetIds;
	}

	public BrapiBaseResource<BrapiGermplasmMarkerProfiles> getMarkerProfilesFor(Context context, String id, int currentPage, int pageSize)
	{
		BrapiBaseResource<BrapiGermplasmMarkerProfiles> result = new BrapiBaseResource<>();

		// Grab the BrapiGermplasm object with the given id, if there is none
		// with the given id return
		BrapiGermplasmMcpd mcpd = getMcpdFor(id).getResult();
		if (mcpd != null)
		{
			BrapiGermplasmMarkerProfiles profiles = new BrapiGermplasmMarkerProfiles();
			profiles.setGermplasmDbId(id);

			List<String> markerProfileIdList = new ArrayList<>();

			// Get the dataset ids of genotype datasets to iterate over
			List<String> dataSetIds = getDataSetIds();

			// Loop over each data set to load the hdf5 file with germplasm data
			dataSetIds.forEach(dataSetId ->
			{
				String hdf5File = HDF5Utils.getHdf5File(dataSetId);

				String folder = context.getParameters().getFirstValue("hdf5-folder");

				// Grab the names of accessions in this dataset from the hdf5
				// file and find those matching our germplasm. The markerprofile
				// id is a concatenation of the dataset id a hyphen and the germplasm id
				try (Hdf5DataExtractor extractor = new Hdf5DataExtractor(new File(folder, hdf5File)))
				{
					List<String> lineNames = extractor.getLines();
					if (lineNames.contains(mcpd.getDefaultDisplayName()))
						markerProfileIdList.add(dataSetId + "-" + id);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			});
			profiles.setMarkerProfiles(markerProfileIdList);
			result = new BrapiBaseResource<BrapiGermplasmMarkerProfiles>(profiles, currentPage, 0, 0);
		}
		return result;
	}

	public static List<String> getSynonyms(ResultSet resultSet)
			throws SQLException
	{
		// Parse out the synonyms
		String synonymsString = resultSet.getString("synonyms");
		if(synonymsString != null)
			return Arrays.asList(synonymsString.split(","));
		else
			return null;
	}

	public BrapiBaseResource<BrapiGermplasmPedigree> getPedigreeById(String id)
	{
		BrapiBaseResource<BrapiGermplasmPedigree> wrappedResult = new BrapiBaseResource<>();

		try (Connection con = Database.INSTANCE.getDataSourceGerminate().getConnection();
			 PreparedStatement statement = DatabaseUtils.createByIdStatement(con, pedigreeByIdQuery, id);
			 ResultSet resultSet = statement.executeQuery())
		{
			if (resultSet.first())
			{
				// Set the ServerGermplasmSearch bean using the data returned from the database
				wrappedResult = new BrapiBaseResource<>(getBrapiGermplasmPedigree(resultSet));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return wrappedResult;
	}

	private BrapiGermplasmPedigree getBrapiGermplasmPedigree(ResultSet resultSet)
		throws SQLException
	{
		BrapiGermplasmPedigree pedigree = new BrapiGermplasmPedigree();
		pedigree.setGermplasmDbId(resultSet.getString("p1.germinatebase_id"));
		pedigree.setDefaultDisplayName(resultSet.getString("name"));
		pedigree.setPedigree(resultSet.getString("definition"));
		pedigree.setParent1Id(resultSet.getString("left_parent"));
		pedigree.setParent2Id(resultSet.getString("right_parent"));

		return pedigree;
	}

	public BrapiListResource<BrapiGermplasmMcpd> getMcpdForSearch(Map<String, List<String>> parameters, int currentPage, int pageSize)
	{
		// Create empty BrapiBaseResource of type BrapiGermplasmMcpd (if for whatever reason we can't get data from the database
		// this is what's returned
		BrapiListResource<BrapiGermplasmMcpd> result = new BrapiListResource<>();

		// Paginate over the data by passing the currentPage and pageSize values to the code which generates the
		// prepared statement (which includes a limit statement)
		try (Connection con = Database.INSTANCE.getDataSourceGerminate().getConnection();
			PreparedStatement statement = DatabaseUtils.createParameterizedLimitStatement(con, getLinesWhere, parameters, currentPage, pageSize);
			ResultSet resultSet = statement.executeQuery())
		{
			List<BrapiGermplasmMcpd> list = new ArrayList<>();

			while (resultSet.next())
				list.add(getBrapiGermplasmMcpd(resultSet));

			long totalCount = DatabaseUtils.getTotalCount(statement);

			// Pass the currentPage and totalCount to the BrapiBaseResource constructor so we generate correct metadata
			result = new BrapiListResource<BrapiGermplasmMcpd>(list, currentPage, pageSize, totalCount);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return result;
	}
}