import java.io.*;
import java.util.StringTokenizer;
import java.util.regex.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.Proxy;

public class Main {

	public Main() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
			throws FileNotFoundException, IOException {
		String thisLine = "";
		BufferedReader reader = new BufferedReader(
				new FileReader(new File("Locales.properties"))
		);
		BufferedWriter writer = new BufferedWriter(
				new FileWriter(new File("Locales_cy.properties"))
		);
		while((thisLine = reader.readLine()) != null) {
			StringTokenizer strtok = new StringTokenizer(thisLine,"=");
			System.out.println(thisLine);
			String prop = strtok.nextToken();
			String text = thisLine.substring(prop.length()+1);
			System.out.println(prop+" :=: "+text);
			String phrase="", token="", translatedText="";
			boolean inPhrase=false, inToken=false, inHtmlTag=false;
			for (int i=0;i<text.length();i++) {
				char c = text.charAt(i);
				if (c=='<') {
					if (inHtmlTag) throw new RuntimeException("Invalid HTML parse");
					inHtmlTag=true;
				} else if (c=='>') {
					if (! inHtmlTag) throw new RuntimeException("Invalid HTML parse");
					inHtmlTag=false;
				}
				if (Character.isLetter(c) || Character.isSpaceChar(c) || c=='.' || c=='\'') {
					if (! inPhrase && inToken && ! inHtmlTag) {
						System.out.println("TOKEN:`"+ token +"'");
						translatedText += token;
						token = "";
					}
					if (! inHtmlTag) {
						inPhrase=true;
						inToken=false;
						phrase += c;
					} else {
						token += c;
					}
				} else {
					if (! inToken && inPhrase && phrase.length() > 0) {
						System.out.println("PHRASE: `"+ phrase +"'");
						String trans = translatePhrase(phrase);
						translatedText += trans;
						System.out.println("TRANSL: `"+ trans +"'");
						phrase = "";
					}
					inToken=true;
					inPhrase=false;
					token += c;
				}
			}
			if (phrase.length() > 0) {
				System.out.println("PHRASE: `"+ phrase +'\'');
				String trans = translatePhrase(phrase);
				translatedText += trans;
				System.out.println("TRANSL: `"+ trans +"'");
			} else if (token.length() > 0) {
				System.out.println("TOKEN: `"+ token +'\'');
				translatedText += token;
			} else if (inHtmlTag) {
				throw new RuntimeException("HTML Parse error");
			}
			String outLine = prop +"="+ translatedText +" (was `"+ text +"')\n"; 
			System.out.print(outLine);
			writer.write(outLine);
			writer.flush();
			try {
				//Thread.sleep(10000);
			} catch (Exception ex) {}
		}
		writer.close();
		reader.close();
	}
	
	private static String translatePhrase(String text)
			throws IOException {
		String beforeNonLetter="", afterNonLetter="";
		if (text.trim().length() == 0)
			return text;
		// Count non-letters at start
		if (! Character.isLetter(text.charAt(0))) {
			int i=0;
			for (i=0;i<text.length();i++) {
				if (Character.isLetter(text.charAt(i))) {
					break;
				}
			}
			beforeNonLetter = text.substring(0,i);
			text = text.substring(i);
		}
		// Count non-letters at end
		if (text.length()>0 && ! Character.isLetter(text.charAt(text.length()-1))) {
			int i=text.length()-1;
			for (i=text.length()-1;i>=0;i--) {
				if (Character.isLetter(text.charAt(i))) {
					break;
				}
			}
			afterNonLetter = text.substring(i+1);
			text = text.substring(0,i+1);
		}
		// Check resultant length
		if (text.length() != text.trim().length())
			throw new RuntimeException("Trimmed string differs when it shouldn't");
		String result = text;
		if (text.length() > 0)
			result = doTranslateText(text);
		return beforeNonLetter+ result +afterNonLetter;
	}
	
	private static String doTranslateText(String text)
			throws IOException {

		String strUrl = "http://www.tranexp.com:2000/InterTran?"
							+ "url=http%3A%2F%2F"
							+ "&type=text"
							+ "&from=eng"
							+ "&to=wel"
							+ "&text="+ URLEncoder.encode(text, "UTF-8");
/*
		String strUrl = "http://www.openaction.net/?"
			+ "url=http%3A%2F%2F"
			+ "&type=text"
			+ "&from=eng"
			+ "&to=wel"
			+ "&text="+ URLEncoder.encode(text, "UTF-8");
*/
		System.out.println("URL: "+strUrl);
		System.getProperties().put( "proxySet", "true" );
		System.getProperties().put( "proxyHost", " cache-haw2.cableinet.co.uk" );
		System.getProperties().put( "proxyPort", "8080" );
		HttpURLConnection url = (HttpURLConnection)(new URL(strUrl)).openConnection();
		url.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; America Online Browser 1.1; rev1.1; Windows NT 5.1;)");
	    url.connect();
	    DataInputStream in = new DataInputStream(url.getInputStream());
	    StringBuffer sb = new StringBuffer();
	    if ( url.getResponseCode() == HttpURLConnection.HTTP_OK ) {
	        try {
	            while (true) {
	              sb.append((char)in.readUnsignedByte());
	            }
	          } catch (EOFException e) {
	          } catch (IOException e) {
	        	  throw new RuntimeException(e + ": " + e.getMessage());
	          }
	    } else {
	    	throw new RuntimeException(url.getResponseMessage());
	    }
	    String doc = sb.toString();
	    //	    regex="</?\w+((\s+\w+(\s*=\s*(?:".*?"|'.*?'|[^'">\s]+))?)+\s*|\s*)/?>";
	    Pattern pattern = Pattern.compile("<textarea.*?name=\"translation\".*?>(.*?)</textarea>");
	    Matcher matcher = pattern.matcher(doc);
	    String result = "";
	    if (matcher.find()) {
		    result = matcher.group(1);
	    } else {
	    	throw new RuntimeException("No translation received");
	    }
		return result;
	}
}
