import net.pontamedia.brandcaptcha.BrandCaptcha;
import net.pontamedia.brandcaptcha.BrandCaptchaFactory;
import net.pontamedia.brandcaptcha.BrandCaptchaResponse;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class ExampleApp {

	
    public static void main(String[] args) {
    	//Warning: this example may not work properly in some versions of firefox (If you see the responses as xml)
    	
    	final String publicKey = "____put_your_public_key____";
    	final String privateKey = "___put_your_private_key___";
		
		
		Spark.setPort(8888);

        Spark.get(new Route("/api/captcha") {
            @Override
            public Object handle(final Request request, final Response response) {
                response.type("text/html");
                String html = "<html><body><form method='post' action='/api/captcha'>";
                html += BrandCaptchaFactory.newBrandCaptcha(
                		publicKey,
                		privateKey)
                        .createBrandCaptchaHtml(null, null);
                html += "<input type='submit' /></form></body></html>";
                return html;
            }
        });

        Spark.post(new Route("/api/captcha") {
            @Override
            public Object handle(final Request request, final Response response) {
                response.type("text/html");
                BrandCaptcha captcha = BrandCaptchaFactory.newBrandCaptcha(
                		publicKey,
                		privateKey);
                BrandCaptchaResponse answer = captcha.checkAnswer(
                        request.host(),
                        request.queryParams("brand_cap_challenge"),
                        request.queryParams("brand_cap_answer"));
                if (answer.isValid()) {
                    return "Woohoo!";
                }
                return answer.getErrorMessage();
            }
        });
    }
}