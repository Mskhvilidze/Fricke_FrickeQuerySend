package fricke.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import fricke.service.Service;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DocsQuickstart {

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Webclient 1";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/json/credentials4.json";

    private DocsQuickstart(){

    }
    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport)
            throws IOException {
        // Load client secrets.
        InputStream in = DocsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            Service.alert("Resource not found: " + CREDENTIALS_FILE_PATH, "InputStream");
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(dataStoreFactory)//new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        //returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static NetHttpTransport netHttpTransport;
    private static FileDataStoreFactory dataStoreFactory;
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("/"),
            ".credentials/drive-java-quickstart");

    static {
        try {
            /*Properties systemProperties = System.getProperties();
            systemProperties.setProperty("http.proxyHost", "proxy.fricke.local");
            systemProperties.setProperty("http.proxyPort", "3128");
            systemProperties.setProperty("https.proxyHost", "proxy.fricke.local");
            systemProperties.setProperty("https.proxyPort", "3128");*/
            netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static HttpTransport newTransport(String proxyUrl, String portStr) throws NumberFormatException, GeneralSecurityException, IOException {
        if (!StringUtils.isEmpty(proxyUrl) && !StringUtils.isEmpty(portStr)) {
            return new NetHttpTransport.Builder()
                    .trustCertificates(GoogleUtils.getCertificateTrustStore())
                    .setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, Integer.parseInt(portStr))))
                    .build();
        }
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        Credential credential = getCredentials(netHttpTransport);
        return new Drive.Builder(netHttpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
