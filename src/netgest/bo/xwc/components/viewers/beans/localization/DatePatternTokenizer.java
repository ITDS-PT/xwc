package netgest.bo.xwc.components.viewers.beans.localization;

import java.util.regex.Pattern;

class DatePatternTokenizer {

	private String pattern;
	private String part1;
	private String part2;
	private String part3;
	private String separator1;
	private String separator2;
	
	public DatePatternTokenizer(String pattern, String[] separator) {
		this.pattern = pattern;
		parse( separator );
	}
	
	void parse(String[] separators) {
		for ( String separator : separators ) {
			String[] parts = this.pattern.split( Pattern.quote( separator ) );
			if (parts.length  == 3) {
				part1 = parts[0];
				part2 = parts[1];
				part3 = parts[2];
				separator1 = separator;
				separator2 = separator;
			}
		}
	}

	public String getPart1() {
		return part1;
	}

	public String getPart2() {
		return part2;
	}

	public String getPart3() {
		return part3;
	}

	public String getSeparator1() {
		return separator1;
	}
	
	public String getSeparator2() {
		return separator2;
	}

}
