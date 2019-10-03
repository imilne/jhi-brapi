package jhi.brapi.client;

import java.util.*;

import jhi.brapi.api.*;
import jhi.brapi.api.allelematrices.*;
import jhi.brapi.api.authentication.*;
import jhi.brapi.api.core.calls.*;
import jhi.brapi.api.genotyping.genomemaps.*;
import jhi.brapi.api.markerprofiles.*;
import jhi.brapi.api.core.studies.*;

import retrofit2.*;
import retrofit2.http.*;

/**
 * A Retrofit service definition of our BrAPI client. This defines the calls
 * that can be used with our client BrAPI client library. Each method defines
 * the URL (relative) to a base URL and the HTTP method that the call will
 * use. In accordance with the Retrofit specification pass null to query
 * parameters that are optional (and for which you don't want to pass values).
 *
 * @see <a href="https://square.github.io/retrofit/">Retrofit</a>
 */
public interface RetrofitService
{
	/**
	 * Queries the BrAPI provider to discover which calls are supported by the
	 * provider. Filterable by the dataType that a call supports returning data
	 * in. Supports paging.
	 *
	 * @param dataType	The datatype of the call return. e.g. JSON, TSV
	 * @param pageSize	The desired size of the returned page
	 * @param page		The desired page of data
	 * @return			A Retrofit Call object which contains a
	 * 					BrapiListResource which wraps a List of BrapiCall
	 * 					objects which contain details of the calls supported by
	 * 					this BrAPI provider
	 */
	@GET("calls")
	Call<BrapiListResource<BrapiCall>> getCalls(@Query("dataType") String dataType, @Query("pageSize") Integer pageSize, @Query("page") Integer page);

	/**
	 * Queries the BrAPI provider to attempt to retrieve an authentication token
	 * using the provided username and password.
	 *
	 * @param tokenPost An BrapiTokenLoginPost object with appropriate user data
	 * @return			A Retrofit Call object which wraps a BrapiSeesionToken
	 * 					object to authenticate the user with the BrAPI provider
	 * 					going forward
	 */
	@POST("token")
	Call<BrapiSessionToken> getAuthToken(@Body BrapiTokenLoginPost tokenPost);

	/**
	 * Searches the BrAPI provider's list of studies, filtering by the studyType
	 * and via an HTTP GET.
	 *
	 * @param studyType	A String defining the type of study to filter by
	 * @param pageSize	The desired size of the returned page
	 * @param page		The desired page of data
	 *
	 * @return			A Retrofit Call object which contains a
	 * 					BrapiListResource which wraps a List of BrapiStudies
	 * 					objects which contain details of the studies which
	 * 					matched the search criteria
	 */
	@GET("studies-search")
	Call<BrapiListResource<Study>> getStudies(@Query("studyType") String studyType, @Query("pageSize") Integer pageSize, @Query("page") Integer page);

	/**
	 * Searches the BrAPI provider's list of matrices via an HTTP GET.
	 *
	 * @param pageSize	The desired size of the returned page
	 * @param page		The desired page of data
	 *
	 * @return			A Retrofit Call object which contains a
	 * 					BrapiListResource which wraps a List of BrapiAlleleMatrixDataset
	 * 					objects which contain details of the matrices which
	 * 					matched the search criteria
	 */
	@GET("allelematrices")
	Call<BrapiListResource<BrapiAlleleMatrixDataset>> getMatrices(@Query("studyDbId") String studyDbId, @Query("pageSize") Integer pageSize, @Query("page") Integer page);

	/**
	 * Searches the BrAPI provider's list of studies, filtering by the
	 * parameters defined in the BrapiStudiesPost object via an HTTP POST.
	 *
	 * @param studiesPost	A BrapiStudiesPost object defining search parameters
	 *
	 * @return				A Retrofit Call object which contains a
	 * 						BrapiListResource which wraps a List of BrapiStudies
	 * 						objects which contain details of the studies which
	 * 						matched the search criteria
	 */
	@POST("studies-search")
	Call<BrapiListResource<Study>> getStudiesPost(@Body BrapiStudiesPost studiesPost);

	/**
	 * Searches the BrAPI provider's list of maps, filtering by the species and
	 * type parameters provided.
	 *
	 * @param species	A String representing the species of the map
	 * @param type		A String representing the type of the map (e.g. Genetic)
	 * @param pageSize	The desired size of the returned page
	 * @param page		The desired page of data
	 *
	 * @return			A Retrofit Call object which contains a
	 * 					BrapiListResource which contains BrapiGenomeMap objects
	 */
	@GET("maps")
	Call<BrapiListResource<GenomeMap>> getMaps(@Query("species") String species, @Query("type") String type, @Query("pageSize") Integer pageSize, @Query("page") Integer page);

	@POST("maps")
	Call<BrapiListResource<GenomeMap>> getMaps(@Body BrapiGenomeMapSearchPost mapSearchPost);

	/**
	 * Gets a list of the BrapiMarkerPosition objects for the BrapiGenomeMap
	 * (Map) specified by {id}.
	 *
	 * @param id				The id of the BrapiGenomeMap the marker positions are for
	 * @param linkageGroupIds	A list of linkage group ids to filter the results by
	 * @param pageSize			The desired size of the returned page
	 * @param page				The desired page of data
	 *
	 * @return					A Retrofit Call object which contains a
	 * 							BrapiListResource which contains BrapiMarkerPositon
	 * 							objects
	 */
	@GET("maps/{id}/positions")
	Call<BrapiListResource<BrapiMarkerPosition>> getMapMarkerData(@Path("id") String id, @Query("linkageGroupId") List<String> linkageGroupIds, @Query("pageSize") Integer pageSize, @Query("page") Integer page);


	/**
	 * Gets a list of BrapiMarkerProfile objects filtered by the parameters
	 * specified.
	 *
	 * @param markerprofileDbId	The markerprofileDbId to filter by
	 * @param studyDbId			The studyDbId to filter by
	 * @param sampleDbId		The sampleDbId to filter by
	 * @param extractDbId		The extractDbId to filter by
	 * @param pageSize			The desired size of the returned page
	 * @param page				The desired page of data
	 *
	 * @return					A Retrofit Call object which contains a
	 * 							BrapiListResource which wraps BrapiMarkerProfile
	 * 							objects
	 */
	@GET("markerprofiles")
	Call<BrapiListResource<BrapiMarkerProfile>> getMarkerProfiles(@Query("markerprofileDbId") String markerprofileDbId, @Query("studyDbId") String studyDbId, @Query("sampleDbId") String sampleDbId, @Query("extractDbId") String extractDbId, @Query("pageSize") Integer pageSize, @Query("page") Integer page);

	/**
	 * Gets a list of BrapiMarkerProfile objects filtered by the parameters
	 * specified in the BrapiMarkerProfilePost object.
	 *
	 * @param markerProfilePost	A BrapiMarkerProfilePost object specifying
	 *                          filtering parameters
	 * @return					A Retrofit Call object which contains a
	 * 							BrapiListResource which wraps BrapiMarkerProfile
	 * 							objects
	 */
	@POST("makrerprofiles-search")
	Call<BrapiListResource<BrapiMarkerProfile>> getMarkerProfiles(@Body BrapiMarkerProfilePost markerProfilePost);

	/**
	 * Gets a matrix of allele calls for markers and markerprofiles in the
	 * specified format, for the specified markerprofiles an other parameters.
	 *
	 * @param markerProfileDbIds	A list of markerprofiles to filter by
	 * @param markerDbId			A list of markers to filter by
	 * @param format				The format of the return (e.g. TSV)
	 * @param expandHomoozygotes	Represent homozygotes as A or AA
	 * @param unknownString			The string to use for unknown data
	 * @param sepPhased				The heterozygote separator for phased data
	 * @param sepUnphased			The heterozygote separtor for unphased data
	 * @param pageSize				The desired size of the returned page
	 * @param page					The desired page of data
	 *
	 * @return						A matrix of allele data in the specified
	 * 								format
	 */
	@FormUrlEncoded
	@POST("allelematrix-search")
	Call<BrapiBaseResource<BrapiAlleleMatrix>> getAlleleMatrix(@Field("markerprofileDbId") List<String> markerProfileDbIds, @Field("markerDbId") List<String> markerDbId, @Field("format") String format, @Field("expandHomozygotes") Boolean expandHomoozygotes, @Field("unknownString") String unknownString, @Field("sepPhased") String sepPhased, @Field("sepUnphased") String sepUnphased, @Field("pageSize") Integer pageSize, @Field("page") Integer page);

	@FormUrlEncoded
	@POST("allelematrix-search")
	Call<BrapiBaseResource<BrapiAlleleMatrix>> getAlleleMatrix(@Field("matrixDbId") String matrixDbId, @Field("format") String format, @Field("expandHomozygotes") Boolean expandHomoozygotes, @Field("unknownString") String unknownString, @Field("sepPhased") String sepPhased, @Field("sepUnphased") String sepUnphased, @Field("pageSize") Integer pageSize, @Field("page") Integer page);


	@POST("allelematrix-search")
	Call<BrapiBaseResource<BrapiAlleleMatrix>> getAlleleMatrix(@Body BrapiAlleleMatrixSearchPost alleleMatrixSearchPost);

	/**
	 *
	 *
	 * @param id
	 * @return
	 */
	@GET("allelematrix-search/status/{id}")
	Call<BrapiListResource<Object>> getAlleleMatrixStatus(@Path("id") String id);
}