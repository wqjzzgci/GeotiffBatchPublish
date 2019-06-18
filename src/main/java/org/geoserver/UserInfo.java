package org.geoserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;

import javax.swing.GroupLayout.SequentialGroup;

import org.apache.log4j.Logger;

public class UserInfo {
	private static Logger log = Logger.getLogger(UserInfo.class);
	private String gspass,gsuser;
	private String restURL;
	private String workspace;
	private String srs;
	private String out_file;
	
	
	    public String getGsuser() {
			return gsuser;
		}
		public void setGsuser(String gsuser) {
			this.gsuser = gsuser;
		}
		public String getGspass(){
	    	return gspass;
	    }
	    public void setGspass(String gspass){
	    	this.gspass =gspass; 
	    }
		/**
		 * @return the restURL
		 */
		public String getRestURL() {
			return restURL;
		}
		/**
		 * @param restURL the restURL to set
		 */
		public void setRestURL(String restURL) {
			this.restURL = restURL;
		}
		/**
		 * @return the workspace
		 */
		public String getWorkspace() {
			return workspace;
		}
		/**
		 * @param workspace the workspace to set
		 */
		public void setWorkspace(String workspace) {
			this.workspace = workspace;
		}
		/**
		 * @return the srs
		 */
		public String getSrs() {
			return srs;
		}
		/**
		 * @param srs the srs to set
		 */
		public void setSrs(String srs) {
			this.srs = srs;
		}
		/**
		 * @return the out_file
		 */
		public String getOut_file() {
			return out_file;
		}
		/**
		 * @param out_file the out_file to set
		 */
		public void setOut_file(String out_file) {
			this.out_file = out_file;
		}	
		
		public static void writeProperties() {  
	        Properties properties = new Properties();  
	        OutputStream output = null;  
	        try {  
	            output = new FileOutputStream("conf.properties");  
	            properties.setProperty("srs", "EPSG:4326");
	            properties.setProperty("restURL","http://localhost:8080/geoserver");  
	            properties.setProperty("gsuser","admin");  
	            properties.setProperty("gspass","geoserver");  
	            properties.setProperty("workspace","landsat8_"); 
	            properties.setProperty("out_file", "e:/WMS.txt");
	            properties.setProperty("filePath","F:/Landsat8/LC80551202015365LGN00");//保存键值对到内存  
	            properties.store(output,"modify time:" + new Date().toString());// 保存键值对到文件中  
	        } catch (IOException io) {  
	            io.printStackTrace();  
	        } finally {  
	            if (output != null) {  
	                try {  
	                    output.close();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	    }  
		
		//从配置文件中获取属性值 （用户名、密码、url、投影坐标系srs、工作区workspace、文件路径）
			 public void loadProperties() {  
			        Properties properties = new Properties();  
			        InputStream input = null;  
			        
			        try {  
			        	
			            input = new FileInputStream("conf.properties");//加载Java项目根路径下的配置文件  
			            properties.load(input);// 加载属性文件  
			           // System.out.println("storeName:" + properties.getProperty("storeName"));  
			           log.info("gsuser:" + properties.getProperty("gsuser"));  
			           log.info("gspass:" + properties.getProperty("gspass"));  
			           log.info("restURL:" + properties.getProperty("restURL"));
			           log.info("srs:" + properties.getProperty("srs")); 
			           log.info("workspace:" + properties.getProperty("workspace"));
			           log.info("out_file:" + properties.getProperty("out_file"));
			           log.info("filePath:" + properties.getProperty("filePath"));
			           
			           this.setGspass(properties.getProperty("gspass"));
			           this.setGsuser(properties.getProperty("gsuser"));
			           this.setSrs(properties.getProperty("srs"));
			           this.setRestURL(properties.getProperty("restURL"));
			           this.setWorkspace(properties.getProperty("workspace"));
			           this.setOut_file(properties.getProperty("out_file"));
			           GetFileName file = new GetFileName();
			           file.setPath(properties.getProperty("filePath"));
			           BatchPublish bp = new BatchPublish();
			   		   bp.publish(file,this);
			        } catch (IOException io) {  
			        } finally {  
			            if (input != null) {  
			                try {  
			                    input.close();  
			                } catch (IOException e) {  
			                    e.printStackTrace();  
			                }  
			            }  
			        }  
			    }
			
			
			
		
		
}
