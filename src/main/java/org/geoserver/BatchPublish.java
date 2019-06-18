package org.geoserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;

public class BatchPublish {
	
	private static Logger log = Logger.getLogger(BatchPublish.class);

	public void publish(GetFileName file,UserInfo user) throws IllegalArgumentException, IOException{
		// @param restURL the base GeoServer URL (e.g.:
		// <TT>http://localhost:8080/geoserver</TT>)
		
		//��ȡGeoServer�û���Ϣ��URL���û��������룬ָ�������ļ�·��
		String restURL=user.getRestURL();
		String gsuser =user.getGsuser();
		String gspass =user.getGspass();
		String workspace = user.getWorkspace();
	    String srs = user.getSrs();
	    String out_file = user.getOut_file();
		String path = file.getPath();
		File fileUrl[] = file.getFileUrl(path);
		//*file.setPath("F:/Landsat8/LC80551202015365LGN00");// ·��
		int i = 0;
		String defaultStyle = "raster";
		String storeName;
		// config layer props (style, ...)
		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.setDefaultStyle(defaultStyle);

		
		GeoServerRESTReader reader = new GeoServerRESTReader(restURL, gsuser, gspass);
		//�ж��Ƿ����ռ��Ѿ�����
		while (reader.existsWorkspace(workspace)) {
			
       /*Ϊ���������ռ��ظ����涨�����ռ�����������������������ռ����ӡ��»��ߡ���������1��
        * ���û�ָ��������û���»��ߣ����ں������.����һ�ι����ռ�Ϊlandsat
        * ��ڶ���Ϊ��landsat_1���Դ�����
        * */
			
			if(workspace.lastIndexOf("_") == -1){
				workspace += "_"+1;
				System.out.println(workspace);
			}else{
		//���û����Ѵ��ڣ������»��ߺ����������1��������־����ʾ			
			log.warn("workspace�Ѵ���,����������workspace,�»�����������");
			String indexStr = workspace.substring(workspace.lastIndexOf("_")+1);
			int index = Integer.parseInt(indexStr);
			index += 1;
			workspace = workspace.substring(0, workspace.lastIndexOf("_")+1)+index;
			   
			}
		}
		
		// Create workspace
		GeoServerRESTPublisher publisher = new GeoServerRESTPublisher(restURL, gsuser, gspass);
		if (publisher.createWorkspace(workspace)) {
		//	System.out.println(" successfully created Workspace");
			log.info(" successfully created Workspace");
		} else {

		//	System.out.println("Error in creating  Workspace");
			log.error("Error in creating  Workspace");
		}
	//---------------------------------------------------------	
		
		
		
		
	//----------------------�����ɺõ�WMS�����wms.txt----------------------------	
		
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		sb.append(data.format(new Date()).toString()+"\r\n");
	//	Properties properties = new Properties();  
    //  OutputStream output = null;  
    //	output = new FileOutputStream("wms.properties"); 
        Writer out = new FileWriter(out_file,true);
        
		//��ȡgeotiff�ļ���
		for (int j = 0; j < fileUrl.length; j++) {
			File geotiff = fileUrl[j];
			String Sgeotiff = fileUrl[j].toString();
		// �ж��ļ���׺���Ƿ�Ϊ.tif
			String tif = ".TIF";
			String suffix = Sgeotiff.substring(Sgeotiff.lastIndexOf("."));
			if (suffix.equalsIgnoreCase(tif)) {
		// storeName ���Ʋ��ܹ�������ᱨ��
				storeName = Sgeotiff.substring(Sgeotiff.lastIndexOf("_"), Sgeotiff.lastIndexOf("."));
		//		System.out.println("geotiffΪ��" + geotiff + "\n" + "storeNameΪ��" + storeName);

		// config coverage props (srs)
				final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
				coverageEncoder.setName(storeName);
				coverageEncoder.setTitle(storeName);
				coverageEncoder.setSRS(srs);
				// coverageEncoder.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED);
				coverageEncoder.setProjectionPolicy(GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED);
				String coverageName = coverageEncoder.getName();

				if (coverageName.isEmpty()) {
					throw new IllegalArgumentException("Unable to run: empty coverage store name");
				}

				// Create Geotiff
				if (publisher.publishExternalGeoTIFF(workspace, storeName, geotiff, coverageName, srs,
						GSResourceEncoder.ProjectionPolicy.REPROJECT_TO_DECLARED, defaultStyle)) {
				//	System.out.println(" successfully created Geotiff");
                        log.info(" successfully created Geotiff");
				} else {

					//System.out.println("Error in creating Geotiff");
					log.error("Error in creating Geotiff");
					
				}

				// create Layer
				if (publisher.configureLayer(workspace, coverageName, layerEncoder)) {

					// GeoServerRESTReader reader;

					try {
						reader = new GeoServerRESTReader(restURL, gsuser, gspass);
						if (reader.getCoverageStore(workspace, storeName) != null) {
					//		System.out.println("Store okey");
							log.info("Store okey");
						} else {
							//System.out.println("Store not okey");
							log.error("Store not okey");

						}
					} catch (MalformedURLException e1) {
						System.out.println(e1);
					}
                          
					//System.out.println("create layer success");
					log.info("create layer success");
                   
					//��ȡwmsURL								    
			            String wmsURL =coverageName+"��wmsURLΪ:"+"http://localhost:8080/geoserver/"+workspace+"/wms?service=WMS&version=1.1.0&request=GetMap&layers="+workspace+":"+coverageName+"\r\n";     
			            sb.append(wmsURL);
			            		       				
			     	//log.info(coverageName+"��wmsURLΪ:"+"http://localhost:8080/geoserver/"+workspace+"/wms?service=WMS&version=1.1.0&request=GetMap&layers="+workspace+":"+coverageName);

				   }
			    }
             }
		         //�����
		         out.write(sb.toString());
		         out.close();
	        
}  
		      
	
		
		
	public static void main(String[] args) throws URISyntaxException, MalformedURLException, FileNotFoundException {
		UserInfo user =new UserInfo();
		user.loadProperties();
		
	}
}