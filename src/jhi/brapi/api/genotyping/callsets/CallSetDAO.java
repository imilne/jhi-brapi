package jhi.brapi.api.genotyping.callsets;

import jhi.brapi.api.*;
import jhi.brapi.api.genotyping.variantsets.*;
import jhi.brapi.util.*;
import org.restlet.resource.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

import static jhi.brapi.util.DatabaseUtils.getLimitStart;

/**
 * Specifies the public interface which any BrapiGenomeMap data accessing classes must implement.
 */
public class CallSetDAO
{
	private final String variantSetsQuery = "SELECT * FROM datasets LEFT JOIN experiments ON " +
		"experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON " +
		"experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.id = 1 AND datasets.source_file IS NOT NULL";

	private final String variantSetsByIdQuery = "SELECT * FROM datasets LEFT JOIN experiments ON " +
		"experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON " +
		"experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.id = 1 AND datasets.id = ? AND " +
		"datasets.source_file IS NOT NULL";

	private final String markerNameIdQuery = "SELECT marker_name, markers.id FROM datasetmembers LEFT JOIN markers ON " +
		"datasetmembers.foreign_id = markers.id WHERE datasetmembers.datasetmembertype_id = 1 AND dataset_id = ?";

	// This code is dense because the mapping between lines and datasets exists in the hdf5 file not the database so
	// there's a fair amount of manipulation to reconstruct that mapping here, particularly with respect to getting
	// pagination across datasets correct
	public BrapiListResource<CallSet> getAll(String dataFolderPath, String variantSetDbId, int currentPage, int pageSize)
	{
		List<VariantSet> list = getVariantSets(dataFolderPath);

		// Optionally filter on the variantSetDbId
		// TODO: think about a better way of doing this
		if (variantSetDbId != null)
			list.removeIf(variantSet -> variantSet.getVariantSetDbId().equals(variantSetDbId) == false);

		TreeMap<Long, String> variantSetIdsByLimitStart = getCallsetIdMap(list);

		long pageStart = DatabaseUtils.getLimitStart(currentPage, pageSize);
		long totalCount = list.stream().mapToLong(VariantSet::getCallSetCount).sum();

		List<CallSet> callSets = new ArrayList<>();

		Map.Entry<Long, String> mapEntry = variantSetIdsByLimitStart.lowerEntry(pageStart);
		if (mapEntry == null)
			mapEntry = variantSetIdsByLimitStart.firstEntry();

		String datasetId = mapEntry.getValue();
		long start = mapEntry.getKey();

		String filename = Hdf55Utils.getHdf5File(datasetId);
		Hdf5DataExtractor hdf5 = new Hdf5DataExtractor(new File(dataFolderPath, filename));

		for (long elementId = pageStart; elementId < pageStart + pageSize; elementId++)
		{
			if (variantSetIdsByLimitStart.containsKey(elementId))
			{
				datasetId = variantSetIdsByLimitStart.get(elementId);
				start = elementId;

				filename = Hdf55Utils.getHdf5File(datasetId);
				hdf5 = new Hdf5DataExtractor(new File(dataFolderPath, filename));
			}

			long internalId = elementId - start;

			callSets.add(getCallSet(hdf5, datasetId, (int)internalId));
		}

		// Pass the currentPage and totalCount to the BrapiBaseResource constructor so we generate correct metadata
		return new BrapiListResource<CallSet>(callSets, currentPage, pageSize, totalCount);
	}

	public BrapiBaseResource<CallSet> getById(String dataFolderPath, String id)
	{
		BrapiBaseResource<CallSet> result = new BrapiBaseResource<>();

		String[] ids = id.split("-");
		if (ids.length == 2)
		{
			String datasetId = ids[0];
			String callSetId = ids[1];

			String filename = Hdf55Utils.getHdf5File(datasetId);
			Hdf5DataExtractor hdf5 = new Hdf5DataExtractor(new File(dataFolderPath, filename));

			result = new BrapiBaseResource<CallSet>(getCallSet(hdf5, datasetId, Integer.parseInt(callSetId)));
		}
		else
		{
			throw new ResourceException(404);
		}

		return result;
	}

	public BrapiMasterDetailResourcePageToken<CallSetCalls> getCallSetCallsById(String dataFolderPath, String id, int currentPage, int pageSize)
	{
		BrapiMasterDetailResourcePageToken<CallSetCalls> result = new BrapiMasterDetailResourcePageToken<>();

		String[] ids = id.split("-");
		if (ids.length == 2)
		{
			String datasetId = ids[0];
			String callSetId = ids[1];

			String filename = Hdf55Utils.getHdf5File(datasetId);
			Hdf5DataExtractor hdf5 = new Hdf5DataExtractor(new File(dataFolderPath, filename));

			CallSetCalls calls = getCallSetCalls(hdf5, Integer.parseInt(callSetId), datasetId, currentPage, pageSize);

			result = new BrapiMasterDetailResourcePageToken<CallSetCalls>(calls, currentPage, pageSize, hdf5.getMarkerCount());
		}
		else
		{
			throw new ResourceException(404);
		}

		return result;
	}

	private CallSet getCallSet(Hdf5DataExtractor hdf5, String datasetId, int internalId)
	{
		CallSet callSet = new CallSet();
		callSet.setCallSetName(hdf5.getLine(internalId));
		callSet.setStudyDbId(datasetId);
		callSet.setCallSetDbId(datasetId + "-" + internalId);
		callSet.setVariantSetIds(Collections.singletonList(datasetId));

		return callSet;
	}

	private CallSetCalls getCallSetCalls(Hdf5DataExtractor hdf5, int lineId, String datasetId, int currentPage, int pageSize)
	{
		String lineName = hdf5.getLine(lineId);

		List<String> markers = hdf5.getMarkers();

		int pageStart = DatabaseUtils.getLimitStart(currentPage, pageSize);

		Map<String, Integer> markerIdsByName = getMarkerIdsByNameMap(datasetId);

		CallSetCalls c = new CallSetCalls();

		List<CallSetCallsDetail> details = new ArrayList<>();

		for (int markerIndex=pageStart; markerIndex < pageStart + pageSize && markerIndex < markers.size(); markerIndex++)
		{
			String geno = hdf5.get(lineId, markerIndex, new GenotypeEncodingParams());
			Genotype genotype = new Genotype();
			genotype.setValues(Collections.singletonList(geno));

			CallSetCallsDetail calls = new CallSetCallsDetail();
			calls.setCallSetDbId(datasetId + "-" + lineId);
			calls.setCallSetName(lineName);
			calls.setGenotype(genotype);
			details.add(calls);

			String markerName = markers.get(markerIndex);
			calls.setVariantName(markerName);

			int markerId = markerIdsByName.get(markerName);
			if (markerId != -1)
				calls.setVariantDbId("" + markerId);
		}

		c.setData(details);

		return c;
	}

	private Map<String, Integer> getMarkerIdsByNameMap(String datasetId)
	{
		Map<String, Integer> markerIdsByName = new HashMap<>();

		try (Connection con = Database.INSTANCE.getDataSourceGerminate().getConnection();
			 PreparedStatement statement = DatabaseUtils.createByIdStatement(con, markerNameIdQuery, datasetId);
			 ResultSet resultSet = statement.executeQuery())
		{
			while (resultSet.next())
			{
				String markerName = resultSet.getString("marker_name");
				int id = resultSet.getInt("id");

				markerIdsByName.put(markerName, id);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return markerIdsByName;
	}

	private TreeMap<Long, String> getCallsetIdMap(List<VariantSet> list)
	{
		TreeMap<Long, String> variantSetIdsByLimitStart = new TreeMap<>();

		long csStart = 0;
		for (VariantSet vs : list)
		{
			variantSetIdsByLimitStart.put(csStart, vs.getVariantSetDbId());
			csStart += vs.getCallSetCount();
		}

		return variantSetIdsByLimitStart;
	}

	private List<VariantSet> getVariantSets(String dataFolderPath)
	{
		List<VariantSet> list = new ArrayList<>();

		try (Connection con = Database.INSTANCE.getDataSourceGerminate().getConnection();
			 PreparedStatement statement = con.prepareStatement(variantSetsQuery);
			 ResultSet resultSet = statement.executeQuery())
		{
			while (resultSet.next())
				list.add(getVariantSetFrom(dataFolderPath, resultSet));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return list;
	}

	private VariantSet getVariantSetFrom(String dataFolderPath, ResultSet resultSet)
		throws SQLException
	{
		VariantSet variantSet = new VariantSet();

		variantSet.setVariantSetDbId(resultSet.getString("datasets.id"));
		variantSet.setStudyDbId(resultSet.getString("datasets.id"));
		variantSet.setVariantSetName(resultSet.getString("description"));

		String filename = Hdf55Utils.getHdf5File(variantSet.getVariantSetDbId());
		Hdf5DataExtractor hdf5 = new Hdf5DataExtractor(new File(dataFolderPath, filename));

		variantSet.setCallSetCount(hdf5.getLineCount());
		variantSet.setVariantCount(hdf5.getMarkerCount());

		return variantSet;
	}
}