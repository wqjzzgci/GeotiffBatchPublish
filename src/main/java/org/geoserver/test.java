package org.geoserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;

public class test {
	public void publish(GetFileName file,UserInfo user) throws FileNotFoundException, IllegalArgumentException, MalformedURLException{
		// @param restURL the base GeoServer URL (e.g.:
		// <TT>http://localhost:8080/geoserver</TT>)
		
		String restURL=user.getRestURL();
		String gsuser =user.getGsuser();
		String gspass =user.getGspass();
		String workspace = user.getWorkspace();
		
		/*String restURL = "http://localhost:8080/geoserver";
	    String gsuser = "admin", gspass = "geoserver";
	    String workspace = "landsat8_";*/
		
		String path = file.getPath();
		File fileUrl[] = file.getFileUrl(path);
		//*file.setPath("F:/Landsat8/LC80551202015365LGN00");// ·��
		int i = 0;
		String srs = "EPSG:3785", defaultStyle = "raster";
		String storeName;
		// config layer props (style, ...)
		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.setDefaultStyle(defaultStyle);

		
		// workspace exist?;�г�workspace
		GeoServerRESTReader reader = new GeoServerRESTReader(restURL, gsuser, gspass);
		List<String> wksn = new ArrayList<String>();
		wksn = reader.getWorkspaceNames();
		ListIterator<String> n = wksn.listIterator();
		while (n.hasNext()) {
			String m = n.next();
			System.out.println(m);

		}
		while (reader.existsWorkspace(workspace)) {
			
			i++;
			System.out.println("workspace�Ѵ���,��������workspace");
			workspace +=i;
		}
		
		// Create workspace
		GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(restURL, gsuser, gspass);
		if (publisher.createWorkspace(workspace)) {
			System.out.println(" successfully created Workspace");
		} else {

			System.out.println("Error in creating  Workspace");
		}
		//��ȡgeotiff�ļ���
		for (int j = 0; j < fileUrl.length; j++) {
			File geotiff = fileUrl[j];
			String Sgeotiff = fileUrl[j].toString();
		// �ж��ļ���׺���Ƿ�Ϊ.tif
			String tif = ".TIF";
			String suffix = Sgeotiff.substring(Sgeotiff.indexOf("."));
			if (suffix.equals(tif)) {
		// storeName ���Ʋ��ܹ�������ᱨ��
				storeName = Sgeotiff.substring(Sgeotiff.indexOf("_"), Sgeotiff.indexOf("."));
		//		System.out.println("geotiffΪ��" + geotiff + "\n" + "storeNameΪ��" + storeName);

		// config coverage props (srs)
				final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
				coverageEncoder.setName(storeName);
				coverageEncoder.setTitle(storeName);
				coverageEncoder.setSRS(srs);
				// coverageEncoder.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED);
				coverageEncoder.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED);
				String coverageName = coverageEncoder.getName();

				if (coverageName.isEmpty()) {
					throw new IllegalArgumentException("Unable to run: empty coverage store name");
				}

				// Create Geotiff
				if (publisher.publishExternalGeoTIFF(workspace, storeName, geotiff, coverageName, srs,
						GSResourceEncoder.ProjectionPolicy.FORCE_DECLARED, defaultStyle)) {
					System.out.println(" successfully created Geotiff");

				} else {

					System.out.println("Error in creating Geotiff");
				}

				// create Layer
				if (publisher.configureLayer(workspace, coverageName, layerEncoder)) {

					// GeoServerRESTReader reader;

					try {
						reader = new GeoServerRESTReader(restURL, gsuser, gspass);
						if (reader.getCoverageStore(workspace, storeName) != null) {
							System.out.println("Store okey");
						} else {
							System.out.println("Store not okey");

						}
					} catch (MalformedURLException e) {
						System.out.println(e);
					}

					System.out.println("create layer success");
					//��ȡbbox
					/*RESTStructuredCoverageGranulesList bbox = new RESTStructuredCoverageGranulesList(null);
					System.out.println(bbox.getBbox()); */
                 //��ȡwmsURL
					System.out.println("http://localhost:8080/geoserver/"+workspace+"/wms?service=WMS&version=1.1.0&request=GetMap&layers="+workspace+":"+coverageName+"&styles=&bbox=528885.0,-801015.0,786915.0,-544485.0&width=768&height=763&srs=EPSG:3785&format=application/openlayers");
				}

			}
	    } 
	}	
	public static void main(String[] args) throws URISyntaxException, MalformedURLException, FileNotFoundException {
		UserInfo user =new UserInfo();
		String restURL = "http://localhost:8080/geoserver";
	    String gsuser = "admin", gspass = "geoserver";
	    String workspace = "landsat8_";
	    String path="d:/Landsat8/LC80551202015365LGN00";
	    user.setGspass(gspass);
	    user.setGsuser(gsuser);
	    user.setWorkspace(workspace);
		user.setRestURL(restURL);
		GetFileName file = new GetFileName();
		file.setPath(path);
		BatchPublish bp = new BatchPublish();
		//bp.publish(file,user);
		
	
		
	}
}
