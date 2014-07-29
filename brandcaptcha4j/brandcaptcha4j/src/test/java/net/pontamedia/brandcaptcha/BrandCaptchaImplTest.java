/*
 * NOTICE
 * 
 * Copyright 2014 Zarego & Pontamedia
 * 
 * This work is a derivative of recaptcha4j
 * 
 */

/*
 * Copyright 2007 Soren Davidsen, Tanesha Networks
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.pontamedia.brandcaptcha;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import net.pontamedia.brandcaptcha.http.HttpLoader;

import org.w3c.dom.Document;

public class BrandCaptchaImplTest extends TestCase {

    BrandCaptchaImpl r;
    MockHttpLoader l;

    @Override
    protected void setUp() throws Exception {
        this.r = new BrandCaptchaImpl();
        this.l = new MockHttpLoader();

        this.r.setPrivateKey("testing");
        this.r.setPublicKey("testing");
        this.r.setBrandCaptchaServer(BrandCaptchaImpl.HTTPS_SERVER);
        this.r.setHttpLoader(this.l);
    }

    public void testCreateCaptchaHtml() {

        String html = this.r.createBrandCaptchaHtml(null, null);
        assertTrue(html.indexOf("<script") != -1);

        String html2 = this.r.createBrandCaptchaHtml("The Error", null);
        assertTrue(html2.indexOf("&amp;error=The+Error") != -1);

        Properties options = new Properties();
        options.setProperty("theme", "mytheme");
        options.setProperty("tabindex", "1");
        String html3 = this.r.createBrandCaptchaHtml("The Error", options);
        assertTrue(html3.indexOf("theme:'mytheme'") != -1);
        assertTrue(html3.indexOf("tabindex:'1'") != -1);
        assertTrue(html3.indexOf(",") != -1);

        // check the shortcut
        String html4 = this.r.createBrandCaptchaHtml("Some Error",
                "othertheme", new Integer(3));
        assertTrue(html4.indexOf("theme:'othertheme'") != -1);
        assertTrue(html4.indexOf("tabindex:'3'") != -1);
        assertTrue(html4.indexOf(",") != -1);

    }

    public void testNotReachable() {

        this.r.setVerifyUrl("http://www.example.com22/");
        BrandCaptchaResponse re = this.r.checkAnswer("123.123.123.123",
                "asdfasdfasdf", "zxcvzxcvzxcv");

        assertTrue(!re.isValid());
        assertEquals("brandcaptcha-not-reachable", re.getErrorMessage());
    }

    public void testAlternativeVerifyUrl() {

        // check that we hit the "correct" verifyurl
        this.l.setNextUrl("http://api.pontamedia.net/verify.php");
        this.r.checkAnswer("123.123.123.123", "asdfasdfasdf", "zxcvzxcvzxcv");

        // check that we now hit the new one.
        this.l.setNextUrl("http://www.example.com/");
        this.r.setVerifyUrl("http://www.example.com/");
        this.r.checkAnswer("123.123.123.123", "asdfasdfasdf", "zxcvzxcvzxcv");
    }

    public void testHtmlIsXhtml() throws Exception {

        String html = this.r.createBrandCaptchaHtml(null, null);

        // wrap the html in a root element.
        html = "<root>" + html + "</root>";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(html.getBytes()));

        // should be OK here.
    }

    public void testCheckAnswer() {
        this.l.setNextReply("true\nnone");

        BrandCaptchaResponse reponse = this.r.checkAnswer("123.123.123.123",
                "abcdefghijklmnop", "response");

        assertTrue(reponse.isValid());
        assertEquals(null, reponse.getErrorMessage());
    }

    public void testCheckAnswer_02() {
        this.l.setNextReply("true\n");

        BrandCaptchaResponse reponse = this.r.checkAnswer("123.123.123.123",
                "abcdefghijklmnop", "response");

        assertTrue(reponse.isValid());
    }

    public void testCheckAnswer_03() {
        this.l.setNextReply("true");

        BrandCaptchaResponse reponse = this.r.checkAnswer("123.123.123.123",
                "abcdefghijklmnop", "response");

        assertTrue(reponse.isValid());
    }

    public void testCheckAnswer_04() {
        this.l.setNextReply("false");

        BrandCaptchaResponse reponse = this.r.checkAnswer("123.123.123.123",
                "abcdefghijklmnop", "response");

        assertFalse(reponse.isValid());
        assertEquals("brandcaptcha4j-missing-error-message",
                reponse.getErrorMessage());

    }

    public void testCheckAnswer_05() {
        this.l.setNextReply("nottrue");

        BrandCaptchaResponse reponse = this.r.checkAnswer("123.123.123.123",
                "abcdefghijklmnop", "response");

        assertFalse(reponse.isValid());
        assertEquals("brandcaptcha4j-missing-error-message",
                reponse.getErrorMessage());

    }

    public void testCheckAnswer_06() {
        this.l.setNextReply("false\nblabla");

        BrandCaptchaResponse reponse = this.r.checkAnswer("123.123.123.123",
                "abcdefghijklmnop", "response");

        assertFalse(reponse.isValid());
        assertEquals("blabla", reponse.getErrorMessage());

    }

    public void testCheckAnswer_07() {
        this.l.setNextReply("false\nblabla\n\n");

        BrandCaptchaResponse reponse = this.r.checkAnswer("123.123.123.123",
                "abcdefghijklmnop", "response");

        assertFalse(reponse.isValid());
        assertEquals("blabla", reponse.getErrorMessage());

    }

    public class MockHttpLoader implements HttpLoader {

        String url;
        String postdata;
        private String reply;

        public void setNextUrl(String url) {
            this.url = url;
        }

        public void setNextPostdata(String postdata) {
            this.postdata = postdata;
        }

        public void setNextReply(String reply) {
            this.reply = reply;
        }

        public String httpGet(String url) {
            if (this.url != null) {
                assertEquals(this.url, url);
            }

            return this.reply;
        }

        public String httpPost(String url, String postdata) {
            if (this.url != null) {
                assertEquals(this.url, url);
            }

            if (this.postdata != null) {
                assertEquals(this.postdata, postdata);
            }

            return this.reply;
        }
    }

}
