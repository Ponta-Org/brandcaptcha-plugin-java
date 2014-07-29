/*
 * NOTICE
 * 
 * Copyright 2014 Zarego & Pontamedia
 * 
 * This work is a derivative of recaptcha4j
 * 
 */

/*
 *
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

import java.util.Properties;

public interface BrandCaptcha {

    /**
     * Creates HTML output with embedded brandcaptcha. The string response
     * should be output on a HTML page (eg. inside a JSP).
     * 
     * @param errorMessage
     *            An errormessage to display in the captcha, null if none.
     * @param options
     *            Options for rendering, <code>tabindex</code> and
     *            <code>theme</code> are currently supported by brandcaptcha.
     *            You can put any options here though, and they will be added to
     *            the BrandCaptchaOptions javascript array.
     * @return
     */
    public String createBrandCaptchaHtml(String errorMessage, Properties options);

    /**
     * Creates HTML output with embedded brandcaptcha. The string response
     * should be output on a HTML page (eg. inside a JSP).
     * 
     * @param errorMessage
     *            The error message to show in the brandcaptcha ouput
     * @param theme
     *            The theme to use for the brandcaptcha output (null if default)
     * @param tabindex
     *            The tabindex to use for the brandcaptcha element (null if
     *            default)
     * @return
     */
    public String createBrandCaptchaHtml(String errorMessage, String theme,
            Integer tabindex);

    /**
     * Validates a BrandCaptcha challenge and response.
     * 
     * @param remoteAddr
     *            The address of the user, eg. request.getRemoteAddr()
     * @param challenge
     *            The challenge from the BrandCaptcha form, this is usually
     *            request.getParameter("brandcaptcha_challenge_field") in your
     *            code.
     * @param response
     *            The response from the BrandCaptcha form, this is usually
     *            request.getParameter("brandcaptcha_response_field") in your
     *            code.
     * @return
     */
    public BrandCaptchaResponse checkAnswer(String remoteAddr,
            String challenge, String response);
}
