
public class ErrorHandle {
	public static boolean checkError(String command) {
		if(command.startsWith("add")) {
			if(!command.contains("(")) {
				return false;
			}
			if(command.lastIndexOf("(") >= command.lastIndexOf(")")) {
				return false;
			}
			return true;
		}
		if(command.startsWith("delete")) {
			if(!checkParenthesis(command)) {
				return false;
			}
			return true;
		}
		if(command.startsWith("out")) {
			if(!checkParenthesis(command)) {
				return false;
			}
			return true;
		}
		if(command.startsWith("in")) {
			if(!checkParenthesis(command) || !checkVariableMath(command)) {
				return false;
			}
			return true;
		}
		if(command.startsWith("rd")) {
			if(!checkParenthesis(command) || !checkVariableMath(command)) {
				return false;
			}
			return true;
		}
		return false;
	}
	public static boolean checkParenthesis(String command) {
		if(!command.contains("(")) {
			return false;
		}
		int left = command.indexOf("(");
		int right = command.lastIndexOf("(");
		if(left != right) {
			return false;
		}
		left = command.indexOf(")");
		right = command.lastIndexOf(")");
		if(left != right) {
			return false;
		}
		left = command.indexOf("(");
		right = command.indexOf(")");
		if(left >= right) {
			return false;
		}
		return true;
	}
	public static boolean checkVariableMath(String command) {
		int start = command.indexOf("(");
		int end = command.indexOf(")");
		command = command.substring(start + 1, end);
		String[] sections = command.split(",");
		for(String section: sections) {
			if(section.startsWith("?")) {
				int indexOfW = section.indexOf("?");
				if(section.indexOf(":") != indexOfW + 2) {
					return false;
				}
				if(section.contains("int")) {
					if((section.indexOf("i") != indexOfW + 1) || section.indexOf("int") != indexOfW + 3) {
						return false;
					}
				}
				if(section.contains("float")) {
					if((section.indexOf("f") != indexOfW + 1) || section.indexOf("float") != indexOfW + 3) {
						return false;
					}
				}
				if(section.contains("string")) {
					if((section.indexOf("s") != indexOfW + 1) || section.indexOf("string") != indexOfW + 3) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
