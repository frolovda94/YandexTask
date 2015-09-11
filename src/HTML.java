import java.io.FileWriter;
import java.io.IOException;


public class HTML {
	
	public static void openDoc(FileWriter out) throws IOException{
		out.write(html_start);
	}
	
	public static void closeDoc(FileWriter out) throws IOException{
		out.write(html_end);
	}
	
	public static void newTable(FileWriter out) throws IOException{
		out.write(html_table_start);
	}
	
	public static void closeTable(FileWriter out) throws IOException{
		out.write(html_table_end);
	}
	
	public static void addRow(FileWriter out, String str) throws IOException{
		out.write("<tr><td>"+str+"</td></tr>\r\n");
	}
	
	public static void addTitle(FileWriter out, String str) throws IOException{
		out.write("<tr><th>"+str+"</th></tr>\r\n");
	}
	
	public static void setModelParameters(FileWriter out, int total, int unclassified, int min_l, int accept_s) throws IOException{
		out.write("<script>\r\n" +
				"document.getElementById(\"total\").innerHTML = \"Number of classes: "+total+"\";\r\n"+
				"document.getElementById(\"unclassified\").innerHTML = \"Unclassified: "+unclassified+"\";\r\n"+
				"document.getElementById(\"params\").innerHTML = \"Model parameters:<br>min_length = "+min_l+"<br>acceptance_step = "+accept_s+"\";\r\n"+
				"</script>\r\n");
	}
	
	private static final String html_start = "<!DOCTYPE html>\r\n" +
			"<html>\r\n" +
			"<head>\r\n" +
			"<style>\r\n" +
			"table, th, td {\r\n" +
			"    border: 1px solid black;\r\n" +
			"    border-collapse: collapse;\r\n" +
			"}\r\n" +
			"th {" +
			"	background-color: #ffff99;" +
			"}" +
			"div: {" +
			"	font-family: \"Times New Roman\", Times, serif;" +
			"	font-size: 100%; " +
			"}" +
			"div:first-letter {" +
			"	font-size: 150%; " +
			"	color: red;" +
			"}" +
			"tr:nth-child(odd){\r\n" +
			"background-color: #eee;\r\n" +
			"}\r\n" +
			"tr:nth-child(even){\r\n" +
			"background-color: #ffffff;\r\n" +
			"}\r\n" +
			"</style>\r\n" +
			"</head>\r\n" +
			"<body>\r\n" +
			"<div><b>Author: Frolov Dmitry\r\n" +
			"<p id = \"total\"></p>\r\n" +
			"<p id = \"unclassified\"></p>\r\n" +
			"<p id = \"params\"></p></b></div>\r\n";
	
	private static final String html_end = "</body>\r\n</html>\r\n";
	private static final String html_table_start = "<table style=\"width:20%; margin-bottom:10px\">\r\n";
	private static final String html_table_end = "</table>\r\n";

}
