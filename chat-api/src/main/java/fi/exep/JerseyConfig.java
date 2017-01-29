package fi.exep;
        
import fi.exep.UserResource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/chat-api")
public class JerseyConfig extends Application {

    public static final String PROPERTIES_FILE = "config.properties";
    public static Properties properties = new Properties();

    static {
        readProperties();
    }

    private static Properties readProperties() {
        InputStream inputStream = JerseyConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*
        for(Object lol : properties.keySet()){
            System.out.println(((String)lol) + " : " + properties.getProperty((String)lol));
        }
         */
        return properties;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
