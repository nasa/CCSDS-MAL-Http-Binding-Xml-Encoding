/*
 * Copyright 2017, by the California Institute of Technology. ALL RIGHTS RESERVED.
 * United States Government Sponsorship acknowledged.
 * Any commercial use must be negotiated with the Office of Technology Transfer at the California Institute of Technology.
 * This software may be subject to U.S. export control laws.
 * By accepting this software, the user agrees to comply with all applicable U.S. export laws and regulations.
 * User has the responsibility to obtain export licenses, or other export authority as may be required
 * before exporting such information to foreign countries or providing access to foreign persons.
 */

package nasa.mo.mal.transport.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import nasa.mo.mal.transport.http.util.Constants;
import nasa.mo.mal.transport.junitcategories.PortDependentTest;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ccsds.moims.mo.mal.MALException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;


/**
 * @author wphyo
 *         Created on 7/27/17.
 */
public class HttpMiniServerTest {
    public static final String KEY_STORE_PASSWORD = "FakePassword";
    @Mock
    private HttpHandler handler;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private HttpMiniServer miniServer;
    private static final String LOCAL_HOST = "localhost";

    @Test
    public void nullHostTest() throws Exception {
        miniServer = HttpMiniServer.custom().build();
        thrown.expect(MALException.class);
        thrown.expectMessage("Null Host, Port, or Handler.");
        miniServer.create();
    }

    @Test
    public void nullPortTest() throws Exception {
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).build();
        thrown.expect(MALException.class);
        thrown.expectMessage("Null Host, Port, or Handler.");
        miniServer.create();
    }

    @Test
    public void nullHandlerTest() throws Exception {
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(8001).build();
        thrown.expect(MALException.class);
        thrown.expectMessage("Null Host, Port, or Handler.");
        miniServer.create();
    }

    @Test
    public void invalidHostTest01() throws Exception {
        miniServer = HttpMiniServer.custom().serverHost("..." + LOCAL_HOST).serverPort(8001).mainHandler(handler)
                .build();
        thrown.expect(MALException.class);
        thrown.expectMessage("nodename nor servname provided, or not known");
        miniServer.create();
    }

    @Test
    public void invalidPortTest01() throws Exception {
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(65536).mainHandler(handler)
                .build();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("port out of range");
        miniServer.create();
    }

    @Test
    public void invalidThreadPoolTest01() throws Exception {
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(8001).mainHandler(handler)
                .threadPoolCount(-1)
                .build();
        thrown.expect(MALException.class);
        thrown.expectMessage("Invalid number of threads");
        miniServer.create();
    }

    @Category(PortDependentTest.class)
    @Test
    public void socketTimeOutTest01() throws Exception {
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler).build();
        miniServer.create();
        HttpGet get = new HttpGet("http://" + LOCAL_HOST + ":" + freePort);
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
        thrown.expect(SocketTimeoutException.class);
        CloseableHttpResponse response = client.execute(get);
    }

    @Category(PortDependentTest.class)
    @Test
    public void successfulTest01() throws Exception {
        mockingHttpHandler();
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler).build();
        miniServer.create();
        miniServer.start();
        HttpGet get = new HttpGet("http://" + LOCAL_HOST + ":" + freePort);
        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
             CloseableHttpResponse response = client.execute(get);) {
            Assert.assertEquals(204, response.getStatusLine().getStatusCode());
        } catch (Exception exp) {
            throw exp;
        }
    }

    @Category(PortDependentTest.class)
    @Test
    public void noConnectionTest01() throws Exception {
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler).build();
        miniServer.create();
        miniServer.start();
        miniServer.stop();
        HttpGet get = new HttpGet("http://" + LOCAL_HOST + ":" + freePort);
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
        thrown.expect(HttpHostConnectException.class);
        CloseableHttpResponse response = client.execute(get);
    }

    @Category(PortDependentTest.class)
    @Test
    public void noConnectionTest02() throws Exception {
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler).build();
        HttpGet get = new HttpGet("http://" + LOCAL_HOST + ":" + freePort);
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
        thrown.expect(HttpHostConnectException.class);
        CloseableHttpResponse response = client.execute(get);
    }

    @Test
    public void readFromFileTest01() throws Exception {
        Assert.assertEquals(null, HttpMiniServer.getPasswordFromFile(null));
        Assert.assertEquals(null, HttpMiniServer.getPasswordFromFile(""));
    }

    @Test
    public void readFromFileTest02() throws Exception {
        thrown.expect(MALException.class);
        thrown.expectMessage("Error read from file.");
        HttpMiniServer.getPasswordFromFile("./InvalidFile");
    }

    @Test
    public void readFromFileTest03() throws Exception {
        File tempFile = tempFolder.newFile("tempFile");
        FileUtils.writeStringToFile(tempFile, "TestString");
        Assert.assertEquals("TestString", HttpMiniServer.getPasswordFromFile(tempFile.getAbsolutePath()));
    }

    @Test
    public void readFromFileTest04() throws Exception {
        File tempFile = tempFolder.newFile("tempFile");
        Assert.assertEquals("", HttpMiniServer.getPasswordFromFile(tempFile.getAbsolutePath()));
    }

    @Test
    public void readFromFileTest05() throws Exception {
        File tempFile = tempFolder.newFile("tempFile");
        FileUtils.writeStringToFile(tempFile, "TestString\n\n\n");
        Assert.assertEquals("TestString", HttpMiniServer.getPasswordFromFile(tempFile.getAbsolutePath()));
    }

    @Test
    public void readFromFileTest06() throws Exception {
        File tempFile = tempFolder.newFile("tempFile");
        FileUtils.writeStringToFile(tempFile, "TestString\n\n\nTestString");
        Assert.assertEquals("TestStringTestString", HttpMiniServer.getPasswordFromFile(tempFile.getAbsolutePath()));
    }

    @Test
    public void readFromFileTest07() throws Exception {
        File tempFile = tempFolder.newFile("tempFile");
        thrown.expect(MALException.class);
        thrown.expectMessage("Error read from file");
        HttpMiniServer.getPasswordFromFile(tempFile.getAbsolutePath() + "1");
    }

    @Category(PortDependentTest.class)
    @Test
    public void secureServerTest01() throws Exception {
        secureServerSetUp();
        mockingHttpHandler();
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler)
                .keyStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").keyStorePassword(KEY_STORE_PASSWORD)
                .trustStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").trustStorePassword(KEY_STORE_PASSWORD)
                .keyStoreType("JKS").isSecureServer(true).build();
        miniServer.create();
        miniServer.start();
        HttpGet get = new HttpGet("https://" + LOCAL_HOST + ":" + freePort);
        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
             CloseableHttpResponse response = client.execute(get);) {
            Assert.assertEquals(204, response.getStatusLine().getStatusCode());
        } catch (Exception exp) {
            thrown.expect(SSLHandshakeException.class);
            throw exp;
        }
    }

    @Test
    public void secureServerTest02() throws Exception {
        secureServerSetUp();
        mockingHttpHandler();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(8001).mainHandler(handler)
                .keyStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").keyStorePassword("WrongPass")
                .trustStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").trustStorePassword("WrongPass")
                .keyStoreType("JKS").isSecureServer(true).build();
        thrown.expect(MALException.class);
        thrown.expectMessage("Error while creating new SSL Context");
        miniServer.create();
    }

    @Category(PortDependentTest.class)
    @Test
    public void notSecureServerTest03() throws Exception {
        secureServerSetUp();
        mockingHttpHandler();
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler)
                .keyStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks")
                .trustStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks")
                .build();
        miniServer.create();
        miniServer.start();
        HttpGet get = new HttpGet("http://" + LOCAL_HOST + ":" + freePort);
        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
             CloseableHttpResponse response = client.execute(get)) {
            Assert.assertEquals(204, response.getStatusLine().getStatusCode());
        } catch (Exception exp) {
            throw exp;
        }
    }


    @Category(PortDependentTest.class)
    @Test
    public void notSecureServerTest04() throws Exception {
        secureServerSetUp();
        mockingHttpHandler();
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler)
                .keyStorePassword(KEY_STORE_PASSWORD)
                .trustStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").trustStorePassword(KEY_STORE_PASSWORD)
                .keyStoreType("JKS").build();
        miniServer.create();
        miniServer.start();
        HttpGet get = new HttpGet("http://" + LOCAL_HOST + ":" + freePort);
        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
             CloseableHttpResponse response = client.execute(get);) {
            Assert.assertEquals(204, response.getStatusLine().getStatusCode());
        } catch (Exception exp) {
            throw exp;
        }
    }

    @Category(PortDependentTest.class)
    @Test
    public void notSecureServerTest05() throws Exception {
        secureServerSetUp();
        mockingHttpHandler();
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler)
                .keyStorePassword(KEY_STORE_PASSWORD)
                .keyStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").trustStorePassword(KEY_STORE_PASSWORD)
                .keyStoreType("JKS").build();
        miniServer.create();
        miniServer.start();
        HttpGet get = new HttpGet("http://" + LOCAL_HOST + ":" + freePort);
        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
             CloseableHttpResponse response = client.execute(get);) {
            Assert.assertEquals(204, response.getStatusLine().getStatusCode());
        } catch (Exception exp) {
            throw exp;
        }
    }

    @Category(PortDependentTest.class)
    @Test
    public void notSecureServerTest06() throws Exception {
        secureServerSetUp();
        mockingHttpHandler();
        int freePort = MessageTestHelper.getAvailablePort();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(freePort).mainHandler(handler)
                .keyStoreFile("")
                .trustStoreFile("")
                .keyStoreType("").build();
        miniServer.create();
        miniServer.start();
        HttpGet get = new HttpGet("http://" + LOCAL_HOST + ":" + freePort);
        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(2000).build()).build();
             CloseableHttpResponse response = client.execute(get);) {
            Assert.assertEquals(204, response.getStatusLine().getStatusCode());
        } catch (Exception exp) {
            throw exp;
        }
    }

    @Test
    public void getSSLContextTest01() throws Exception {
        SSLContext context1 = HttpMiniServer.getSSLContext("", null, "", null, Constants.KEY_STORE_TYPE);
        SSLContext context2 = HttpMiniServer.getSSLContext("", null, "", null, Constants.KEY_STORE_TYPE);
        SSLContext context3 = HttpMiniServer.getSSLContext("", "test", "test", "test", Constants.KEY_STORE_TYPE);
        SSLContext context4 = HttpMiniServer.getSSLContext("test", null, "test", "test", "");
        SSLContext context5 = HttpMiniServer.getSSLContext("test", "test", "", "test", Constants.KEY_STORE_TYPE);
        SSLContext context6 = HttpMiniServer.getSSLContext("test", "test", "test", null, "");
        Assert.assertEquals(context1, context2);
        Assert.assertEquals(context1, context3);
        Assert.assertEquals(context1, context4);
        Assert.assertEquals(context1, context5);
        Assert.assertEquals(context1, context6);
    }

    /**
     * Server is setup saying it is a secure server.
     * But doesn't provide keystore files or password.
     * Expecting a MALException
     * @throws Exception other unexpected exception
     */
    @Test
    public void secureServerExceptionTest01() throws Exception {
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(8001)
                .mainHandler(handler).isSecureServer(true).build();
        thrown.expect(MALException.class);
        thrown.expectMessage("Null values in necessary keystore and password files");
        miniServer.create();
    }

    /**
     * Testing isSecureServer flag is working correctly. 
     * 1. setting the flag to FALSE, and provide necessary info to create Secure server. 
     *      result = normal server
     * 2. setting the flag to TRUE, and provide necessary info to create Secure server. 
     *      result = secure server
     *      
     * @throws Exception any unexpected exception
     */
    @Category(PortDependentTest.class)
    @Test
    public void notSecureServerCreatedTest01() throws Exception {
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(MessageTestHelper.getAvailablePort())
                .mainHandler(handler).keyStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").keyStorePassword(KEY_STORE_PASSWORD)
                .trustStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").trustStorePassword(KEY_STORE_PASSWORD)
                .keyStoreType("JKS").isSecureServer(false).build();
        miniServer.create();
        Field serverField = miniServer.getClass().getDeclaredField("server");
        serverField.setAccessible(true);
        HttpServer server = (HttpServer) serverField.get(miniServer);
        Assert.assertFalse(server instanceof HttpsServer);
        
        secureServerSetUp();
        miniServer = HttpMiniServer.custom().serverHost(LOCAL_HOST).serverPort(MessageTestHelper.getAvailablePort())
                .mainHandler(handler).keyStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").keyStorePassword(KEY_STORE_PASSWORD)
                .trustStoreFile(tempFolder.getRoot().getAbsolutePath() + "/server.jks").trustStorePassword(KEY_STORE_PASSWORD)
                .keyStoreType("JKS").isSecureServer(true).build();
        miniServer.create();
        serverField = miniServer.getClass().getDeclaredField("server");
        serverField.setAccessible(true);
        server = (HttpServer) serverField.get(miniServer);
        Assert.assertTrue(server instanceof HttpsServer);
    }

    /**
     * Creating password file, keystore file, and generating a key.
     * Necessary for creating secure server
     *
     * @throws IOException required at FileUtils.writeStringToFile
     * @throws InterruptedException required at Process.waitFor
     */
    private void secureServerSetUp() throws IOException, InterruptedException {
        File tempFile = tempFolder.newFile("store.pass");
        FileUtils.writeStringToFile(tempFile, KEY_STORE_PASSWORD);
        String keyTool = "echo | keytool -genkey -keyalg RSA -keysize 2048 -alias client -keystore server.jks " +
                "-storepass:file store.pass -validity 360 -ext san=dns:localhost " +
                "-dname \"CN=test,OU=test,O=test,L=test,ST=test,C=test\"";
        ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", keyTool);
        builder.directory(tempFolder.getRoot());
        Process p = builder.start();
        p.waitFor();
    }

    /**
     * Creating simple handler for get requests by returning 204.
     *
     * @throws IOException required at HttpHandler.handle()
     */
    private void mockingHttpHandler() throws IOException {
        doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            if (arguments != null && arguments.length == 1 && arguments[0] != null) {
                HttpExchange exchange = (HttpExchange) arguments[0];
                Assert.assertEquals(exchange.getRequestMethod(), "GET");
                exchange.sendResponseHeaders(204, -1);
            } else {
                Assert.assertTrue(false);
            }
            return null;
        }).when(handler).handle(any(HttpExchange.class));
    }
}
