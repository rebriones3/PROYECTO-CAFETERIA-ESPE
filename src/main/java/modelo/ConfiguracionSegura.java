package modelo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfiguracionSegura {
    private static Properties props = new Properties();
    private static boolean cargado = false;
    
    public static void cargarConfiguracion() {
        if (cargado) return;
        
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
            cargado = true;
            return;
        } catch (IOException e) {
            // Usar variables de entorno del sistema o valores por defecto
        }
        
        props.setProperty("MONGODB_URI", 
            getEnvOrDefault("MONGODB_URI", "mongodb://localhost:27017"));
        props.setProperty("MONGODB_DATABASE", 
            getEnvOrDefault("MONGODB_DATABASE", "cafeteria_espe"));
        props.setProperty("MONGODB_USER", 
            getEnvOrDefault("MONGODB_USER", ""));
        props.setProperty("MONGODB_PASSWORD", 
            getEnvOrDefault("MONGODB_PASSWORD", ""));
        props.setProperty("ADMIN_EMAIL", 
            getEnvOrDefault("ADMIN_EMAIL", "admin@espe.edu.ec"));
        props.setProperty("ADMIN_PASSWORD", 
            getEnvOrDefault("ADMIN_PASSWORD", ""));
        
        cargado = true;
    }
    
    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    
    public static String obtener(String clave) {
        if (!cargado) cargarConfiguracion();
        return props.getProperty(clave, "");
    }
    
    public static String getMongoDBUri() {
        return obtener("MONGODB_URI");
    }
    
    public static String getMongoDBDatabase() {
        return obtener("MONGODB_DATABASE");
    }
    
    public static String getMongoDBUser() {
        return obtener("MONGODB_USER");
    }
    
    public static String getMongoDBPassword() {
        return obtener("MONGODB_PASSWORD");
    }
    
    public static String getAdminEmail() {
        return obtener("ADMIN_EMAIL");
    }
    
    public static String getAdminPassword() {
        return obtener("ADMIN_PASSWORD");
    }
    
    public static String getMongoDBConnectionString() {
        String user = getMongoDBUser();
        String password = getMongoDBPassword();
        String uri = getMongoDBUri();
        
        if (!user.isEmpty() && !password.isEmpty()) {
            String host = uri.replace("mongodb://", "");
            return "mongodb://" + user + ":" + password + "@" + host;
        }
        
        return uri;
    }
}
