package pl.cp.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;

import pl.cp.utils.HibernateUtil;

public class App {
	
	public static final String PROJECT_PATH = "/xxx/GuineaPigSVN";

	private final Logger logger = Logger.getLogger("App");
	
	public static void main(String[] args) {
		App app = new App();
		app.logger.info("STARTING EXECUTE SQL FILES");
		
		HibernateUtil.createSessionFactory();
		for(File f : app.getSqlFiles()){
			app.executeSql(f);
		}
		HibernateUtil.getSessionFactory().close();
		app.logger.info("EXECUTING SQL FILES ENDED SUCCESSFULLY");
	}
	
	private List<File> getSqlFiles(){
		Runtime run = Runtime.getRuntime();
		try {
			List<File> sqlFiles = new ArrayList<File>();
			String command = "cd " + PROJECT_PATH;
			command += "&& svn log --verbose -r HEAD --username user --password pass";
			Process proc = run.exec(new String[]{"/bin/sh", "-c", command});
			proc.waitFor();
			String str;
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while(br.ready()){
				str = br.readLine();
				if(str.matches(".*\\.sql$")){
					File f = new File(PROJECT_PATH + str.replaceFirst(".* /branches", ""));
					sqlFiles.add(f);
				}
			}
			if(sqlFiles.size() > 0){
				return sqlFiles;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String readSqlFile(File sqlFile){
		String line = null;
		try {
			InputStream in = new FileInputStream(sqlFile);
			InputStreamReader reader = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(reader);
			StringBuffer sb = new StringBuffer();
			while(br.ready()){
				String s = br.readLine();
				if(s != null){
					sb.append(s);
				}
			}
			line = sb.toString();
		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	private void executeSql(File file){
		String sql = readSqlFile(file);
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.getTransaction().begin();
		try{
			session.createSQLQuery(sql).executeUpdate();
			session.getTransaction().commit();
			logger.info(file.getName() + " UPDATE SUCCESS");
		}catch(Exception e){
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		session.close();
	}
}
