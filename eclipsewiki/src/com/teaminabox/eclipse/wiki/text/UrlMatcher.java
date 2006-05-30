package com.teaminabox.eclipse.wiki.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teaminabox.eclipse.wiki.WikiConstants;
import com.teaminabox.eclipse.wiki.editors.WikiDocumentContext;

/**
 * I match URLs. A match occurs when text starts with a {@link WikiConstants#URL_PREFIXES url prefix}and ends with
 * white space.
 * 
 * @see WikiConstants#URL_PREFIXES
 */
public final class UrlMatcher extends AbstractTextRegionMatcher {

	/**
	 * regex for matching urls.
	 * @see http://www.foad.org/~abigail/Perl/url2.html
	 * @see http://www.foad.org/~abigail/Perl/url3.regex
	 */
	private static final String URL_REGEX = "(?:http://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\." + 
			")*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)" + 
			"){3}))(?::(?:\\d+))?)(?:/(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F" + 
			"\\d]{2}))|[;:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{" + 
			"2}))|[;:@&=])*))*)(?:\\?(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{" + 
			"2}))|[;:@&=])*))?)?)|(?:ftp://(?:(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?" + 
			":%[a-fA-F\\d]{2}))|[;?&=])*)(?::(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-" + 
			"fA-F\\d]{2}))|[;?&=])*))?@)?(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-" + 
			")*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?" + 
			":\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?))(?:/(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!" + 
			"*\'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'()" + 
			",]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*))*)(?:;type=[AIDaid])?)?)|(?:news:(?:" + 
			"(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[;/?:&=])+@(?:(?:(" + 
			"?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[" + 
			"a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3})))|(?:[a-zA-Z](" + 
			"?:[a-zA-Z\\d]|[_.+-])*)|\\*))|(?:nntp://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[" + 
			"a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d" + 
			"])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)/(?:[a-zA-Z](?:[a-zA-Z" + 
			"\\d]|[_.+-])*)(?:/(?:\\d+))?)|(?:telnet://(?:(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+" + 
			"!*\'(),]|(?:%[a-fA-F\\d]{2}))|[;?&=])*)(?::(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'()" + 
			",]|(?:%[a-fA-F\\d]{2}))|[;?&=])*))?@)?(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a" + 
			"-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d]" + 
			")?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?))/?)|(?:gopher://(?:(?:" + 
			"(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:" + 
			"(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+" + 
			"))?)(?:/(?:[a-zA-Z\\d$\\-_.+!*\'(),;/?:@&=]|(?:%[a-fA-F\\d]{2}))(?:(?:(?:[" + 
			"a-zA-Z\\d$\\-_.+!*\'(),;/?:@&=]|(?:%[a-fA-F\\d]{2}))*)(?:%09(?:(?:(?:[a-zA" + 
			"-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*)(?:%09(?:(?:[a-zA-Z\\d$" + 
			"\\-_.+!*\'(),;/?:@&=]|(?:%[a-fA-F\\d]{2}))*))?)?)?)?)|(?:wais://(?:(?:(?:" + 
			"(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:" + 
			"[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?" + 
			")/(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))*)(?:(?:/(?:(?:[a-zA" + 
			"-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))*)/(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(" + 
			"?:%[a-fA-F\\d]{2}))*))|\\?(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]" + 
			"{2}))|[;:@&=])*))?)|(?:mailto:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),;/?:@&=]|(?:%" + 
			"[a-fA-F\\d]{2}))+))|(?:file://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]" + 
			"|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:" + 
			"(?:\\d+)(?:\\.(?:\\d+)){3}))|localhost)?/(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'()" + 
			",]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(" + 
			"?:%[a-fA-F\\d]{2}))|[?:@&=])*))*))|(?:prospero://(?:(?:(?:(?:(?:[a-zA-Z" + 
			"\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)" + 
			"*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)/(?:(?:(?:(?" + 
			":[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*)(?:/(?:(?:(?:[a-" + 
			"zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&=])*))*)(?:(?:;(?:(?:(?:[" + 
			"a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&])*)=(?:(?:(?:[a-zA-Z\\d" + 
			"$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[?:@&])*)))*)|(?:ldap://(?:(?:(?:(?:" + 
			"(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:" + 
			"[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?" + 
			"))?/(?:(?:(?:(?:(?:(?:(?:[a-zA-Z\\d]|%(?:3\\d|[46][a-fA-F\\d]|[57][Aa\\d])" + 
			")|(?:%20))+|(?:OID|oid)\\.(?:(?:\\d+)(?:\\.(?:\\d+))*))(?:(?:%0[Aa])?(?:%2" + 
			"0)*)=(?:(?:%0[Aa])?(?:%20)*))?(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F" + 
			"\\d]{2}))*))(?:(?:(?:%0[Aa])?(?:%20)*)\\+(?:(?:%0[Aa])?(?:%20)*)(?:(?:(?" + 
			":(?:(?:[a-zA-Z\\d]|%(?:3\\d|[46][a-fA-F\\d]|[57][Aa\\d]))|(?:%20))+|(?:OID" + 
			"|oid)\\.(?:(?:\\d+)(?:\\.(?:\\d+))*))(?:(?:%0[Aa])?(?:%20)*)=(?:(?:%0[Aa])" + 
			"?(?:%20)*))?(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))*)))*)(?:(" + 
			"?:(?:(?:%0[Aa])?(?:%20)*)(?:[;,])(?:(?:%0[Aa])?(?:%20)*))(?:(?:(?:(?:(" + 
			"?:(?:[a-zA-Z\\d]|%(?:3\\d|[46][a-fA-F\\d]|[57][Aa\\d]))|(?:%20))+|(?:OID|o" + 
			"id)\\.(?:(?:\\d+)(?:\\.(?:\\d+))*))(?:(?:%0[Aa])?(?:%20)*)=(?:(?:%0[Aa])?(" + 
			"?:%20)*))?(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))*))(?:(?:(?:" + 
			"%0[Aa])?(?:%20)*)\\+(?:(?:%0[Aa])?(?:%20)*)(?:(?:(?:(?:(?:[a-zA-Z\\d]|%(" + 
			"?:3\\d|[46][a-fA-F\\d]|[57][Aa\\d]))|(?:%20))+|(?:OID|oid)\\.(?:(?:\\d+)(?:" + 
			"\\.(?:\\d+))*))(?:(?:%0[Aa])?(?:%20)*)=(?:(?:%0[Aa])?(?:%20)*))?(?:(?:[a" + 
			"-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))*)))*))*(?:(?:(?:%0[Aa])?(?:%2" + 
			"0)*)(?:[;,])(?:(?:%0[Aa])?(?:%20)*))?)(?:\\?(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+" + 
			"!*\'(),]|(?:%[a-fA-F\\d]{2}))+)(?:,(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-f" + 
			"A-F\\d]{2}))+))*)?)(?:\\?(?:base|one|sub)(?:\\?(?:((?:[a-zA-Z\\d$\\-_.+!*\'(" + 
			"),;/?:@&=]|(?:%[a-fA-F\\d]{2}))+)))?)?)?)|(?:(?:z39\\.50[rs])://(?:(?:(?" + 
			":(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?" + 
			":[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))" + 
			"?)(?:/(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))+)(?:\\+(?:(?:" + 
			"[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))+))*(?:\\?(?:(?:[a-zA-Z\\d$\\-_" + 
			".+!*\'(),]|(?:%[a-fA-F\\d]{2}))+))?)?(?:;esn=(?:(?:[a-zA-Z\\d$\\-_.+!*\'()," + 
			"]|(?:%[a-fA-F\\d]{2}))+))?(?:;rs=(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA" + 
			"-F\\d]{2}))+)(?:\\+(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))+))*)" + 
			"?))|(?:cid:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[;?:@&=" + 
			"])*))|(?:mid:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[;?:@" + 
			"&=])*)(?:/(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[;?:@&=]" + 
			")*))?)|(?:vemmi://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z" + 
			"\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\" + 
			".(?:\\d+)){3}))(?::(?:\\d+))?)(?:/(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a" + 
			"-fA-F\\d]{2}))|[/?:@&=])*)(?:(?:;(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a" + 
			"-fA-F\\d]{2}))|[/?:@&])*)=(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d" + 
			"]{2}))|[/?:@&])*))*))?)|(?:imap://(?:(?:(?:(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+" + 
			"!*\'(),]|(?:%[a-fA-F\\d]{2}))|[&=~])+)(?:(?:;[Aa][Uu][Tt][Hh]=(?:\\*|(?:(" + 
			"?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[&=~])+))))?)|(?:(?:;[" + 
			"Aa][Uu][Tt][Hh]=(?:\\*|(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2" + 
			"}))|[&=~])+)))(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[" + 
			"&=~])+))?))@)?(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])" + 
			"?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:" + 
			"\\d+)){3}))(?::(?:\\d+))?))/(?:(?:(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:" + 
			"%[a-fA-F\\d]{2}))|[&=~:@/])+)?;[Tt][Yy][Pp][Ee]=(?:[Ll](?:[Ii][Ss][Tt]|" + 
			"[Ss][Uu][Bb])))|(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))" + 
			"|[&=~:@/])+)(?:\\?(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[" + 
			"&=~:@/])+))?(?:(?:;[Uu][Ii][Dd][Vv][Aa][Ll][Ii][Dd][Ii][Tt][Yy]=(?:[1-" + 
			"9]\\d*)))?)|(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[&=~" + 
			":@/])+)(?:(?:;[Uu][Ii][Dd][Vv][Aa][Ll][Ii][Dd][Ii][Tt][Yy]=(?:[1-9]\\d*" + 
			")))?(?:/;[Uu][Ii][Dd]=(?:[1-9]\\d*))(?:(?:/;[Ss][Ee][Cc][Tt][Ii][Oo][Nn" + 
			"]=(?:(?:(?:[a-zA-Z\\d$\\-_.+!*\'(),]|(?:%[a-fA-F\\d]{2}))|[&=~:@/])+)))?))" + 
			")?)|(?:nfs:(?:(?://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-" + 
			"Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:" + 
			"\\.(?:\\d+)){3}))(?::(?:\\d+))?)(?:(?:/(?:(?:(?:(?:(?:[a-zA-Z\\d\\$\\-_.!~*\'" + 
			"(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\-_.!~*\'()," + 
			"])|(?:%[a-fA-F\\d]{2})|[:@&=+])*))*)?)))?)|(?:/(?:(?:(?:(?:(?:[a-zA-Z\\d" + 
			"\\$\\-_.!~*\'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*)(?:/(?:(?:(?:[a-zA-Z\\d\\$\\" + 
			"-_.!~*\'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*))*)?))|(?:(?:(?:(?:(?:[a-zA-" + 
			"Z\\d\\$\\-_.!~*\'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*)(?:/(?:(?:(?:[a-zA-Z\\d" + 
			"\\$\\-_.!~*\'(),])|(?:%[a-fA-F\\d]{2})|[:@&=+])*))*)?)))";
	
	private final Pattern pattern;
	
	public UrlMatcher() {
		pattern	= Pattern.compile(URL_REGEX);
	}
	
	public TextRegion createTextRegion(String text, WikiDocumentContext context) {
		if (accepts(text, context)) {
			return new UrlTextRegion(new String(text.substring(0, matchLength(text, context))));
		}
		return null;
	}
	
	protected boolean accepts(char c, boolean firstCharacter) {
		if (firstCharacter) {
			for (int i = 0; i < WikiConstants.URL_PREFIXES.length; i++) {
				if (c == WikiConstants.URL_PREFIXES[i].charAt(0)) {
					return true;
				}
			}
		}
		return c != ' ';
	}
	
	private boolean accepts(String text, WikiDocumentContext context) {
		Matcher m = pattern.matcher(text);
		return m.find() && m.start() == 0;
	}

	private int matchLength(String text, WikiDocumentContext context) {
		Matcher m = pattern.matcher(text);
		m.find();
		return m.end();
	}

}