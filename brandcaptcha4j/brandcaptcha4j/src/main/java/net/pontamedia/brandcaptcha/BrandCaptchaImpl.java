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

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

import net.pontamedia.brandcaptcha.http.HttpLoader;
import net.pontamedia.brandcaptcha.http.SimpleHttpLoader;

public class BrandCaptchaImpl implements BrandCaptcha {

    public static final String PROPERTY_THEME = "theme";
    public static final String PROPERTY_TABINDEX = "tabindex";

    public static final String HTTP_SERVER = "http://api.pontamedia.net";
    public static final String HTTPS_SERVER = "https://api.pontamedia.net";
    public static final String VERIFY_URL = "http://api.pontamedia.net/verify.php";

    private String privateKey;
    private String publicKey;
    private String brandCaptchaServer = HTTP_SERVER;
    private String verifyUrl = VERIFY_URL;
    
    private HttpLoader httpLoader = new SimpleHttpLoader();

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setBrandCaptchaServer(String brandCaptchaServer) {
        this.brandCaptchaServer = brandCaptchaServer;
    }


    public void setVerifyUrl(String verifyUrl) {
        this.verifyUrl = verifyUrl;
    }

    public void setHttpLoader(HttpLoader httpLoader) {
        this.httpLoader = httpLoader;
    }

    public BrandCaptchaResponse checkAnswer(String remoteAddr,
            String challenge, String response) {

        String postParameters = "privatekey="
                + URLEncoder.encode(this.privateKey) + "&remoteip="
                + URLEncoder.encode(remoteAddr) + "&challenge="
                + URLEncoder.encode(challenge) + "&response="
                + URLEncoder.encode(response);

        final String message;
        try {
            message = this.httpLoader.httpPost(this.verifyUrl, postParameters);

            if (message == null) {
                return new BrandCaptchaResponse(false,
                        "brandcaptcha-not-reachable");
            }
        } catch (BrandCaptchaException networkProblem) {
            return new BrandCaptchaResponse(false, "brandcaptcha-not-reachable");
        }

        String[] a = message.split("\r?\n");
        if (a.length < 1) {
            return new BrandCaptchaResponse(false,
                    "No answer returned from brandcaptcha: " + message);
        }
        boolean valid = "true".equals(a[0]);
        String errorMessage = null;
        if (!valid) {
            if (a.length > 1) {
                errorMessage = a[1];
            } else {
                errorMessage = "brandcaptcha4j-missing-error-message";
            }
        }

        return new BrandCaptchaResponse(valid, errorMessage);
    }

    public String createBrandCaptchaHtml(String errorMessage, Properties options) {

        String errorPart = errorMessage == null ? "" : "&amp;error="
                + URLEncoder.encode(errorMessage);

        String message = this.fetchJSOptions(options);

        message += "<script type=\"text/javascript\" src=\""
                + this.brandCaptchaServer + "/challenge.php?k="
                + this.publicKey + errorPart + "\"></script>\r\n";

        return message;
    }

    public String createBrandCaptchaHtml(String errorMessage, String theme,
            Integer tabindex) {

        Properties options = new Properties();

        if (theme != null) {
            options.setProperty(PROPERTY_THEME, theme);
        }
        if (tabindex != null) {
            options.setProperty(PROPERTY_TABINDEX, String.valueOf(tabindex));
        }

        return this.createBrandCaptchaHtml(errorMessage, options);
    }

    /**
     * Produces javascript array with the BrandCaptchaOptions encoded.
     * 
     * @param properties
     * @return
     */
    private String fetchJSOptions(Properties properties) {

        if (properties == null || properties.size() == 0) {
            return "";
        }

        String jsOptions = "<script type=\"text/javascript\">\r\n"
                + "var BrandCaptchaOptions = {";

        for (Enumeration e = properties.keys(); e.hasMoreElements();) {
            String property = (String) e.nextElement();

            jsOptions += property + ":'" + properties.getProperty(property)
                    + "'";

            if (e.hasMoreElements()) {
                jsOptions += ",";
            }

        }

        jsOptions += "};\r\n</script>\r\n";

        return jsOptions;
    }
}
