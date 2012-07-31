package netgest.bo.xwc.components.classic.autocomplete;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * Helper class for the AttributeAutoComplete component to help find
 * attribute names in a template
 * 
 * @author PedroRio
 *
 */
public class FindAttributesInTemplate {

	//Matches anything inside brackets, retrieves the word with the brackets
	private static Pattern findWordsInBrackets = Pattern.compile( "\\{(.*?)\\}",Pattern.DOTALL );;
	
	public List<String> findAttributes( String template ){
		Matcher m = findWordsInBrackets.matcher( template );
		List<String> attributesFound = new LinkedList<String>();
		
		while (m.find()){
			String attributeWithBrackets = m.group();
			attributesFound.add( removeEnclosingBrackets( attributeWithBrackets ) );
		}
		return attributesFound;
	}

	private String removeEnclosingBrackets( String group ) {
		return group.substring( 1, group.length() - 1 ).trim();
	}
	
}
