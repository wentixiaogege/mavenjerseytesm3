package com.sampullara;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.junit.After;
import org.junit.Before;

import junit.framework.TestCase;

import com.sampullara.ProtobufProviders.ProtobufMessageBodyReader;
import com.sampullara.ProtobufProviders.ProtobufMessageBodyWriter;
import com.sampullara.jaxrsprotobuf.tutorial.AddressBookProtos;
//import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.net.httpserver.HttpServer;
/**
 * TODO: Edit this
 * <p/>
 * User: sam
 * Date: Dec 27, 2008
 * Time: 5:10:58 PM
 */
public class MainTest extends TestCase {
    private HttpServer server;
    private WebResource r;

    
    @Before
    protected void setUp() throws Exception {
        super.setUp();

        //start the Grizzly web container and create the client
        server = Main.createServer(Main.BASE_URI);
        server.start();
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(ProtobufMessageBodyReader.class);
        cc.getClasses().add(ProtobufMessageBodyWriter.class);
        Client c = Client.create(cc);
        r = c.resource(Main.BASE_URI);
    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();
        server.stop(0);
    }

   /* public void testUsingJerseyClient() {
        WebResource wr = r.path("person");
        AddressBookProtos.Person p = null;
		try {
			p = wr.get(AddressBookProtos.Person.class);
		} catch (UniformInterfaceException e) {
			e.printStackTrace();
		}
        assertEquals("Sam", p.getName());
        System.out.println("-----------------------------------------------------------------------------------------"+p.getName());
        AddressBookProtos.Person p2 = wr.type("application/x-protobuf").post(AddressBookProtos.Person.class, p);
        assertEquals(p, p2);
    }
*/
    public void testUsingURLConnection() throws IOException {
        AddressBookProtos.Person person;
        {
            URL url = new URL("http://localhost:9998/person");
            URLConnection urlc = url.openConnection();
            urlc.setDoInput(true);
            urlc.setRequestProperty("Accept", "application/x-protobuf");
            person = AddressBookProtos.Person.newBuilder().mergeFrom(urlc.getInputStream()).build();
            assertEquals("Sam", person.getName());
        }
        {
            URL url = new URL("http://localhost:9998/person");
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setDoInput(true);
            urlc.setDoOutput(true);
            urlc.setRequestMethod("POST");
            urlc.setRequestProperty("Accept", "application/x-protobuf");
            urlc.setRequestProperty("Content-Type", "application/x-protobuf");
            person.writeTo(urlc.getOutputStream());
            AddressBookProtos.Person person2 = AddressBookProtos.Person.newBuilder().mergeFrom(urlc.getInputStream()).build();
            assertEquals(person, person2);
        }
    }

}
