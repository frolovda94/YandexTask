import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * �����: ������ �������
 * ����: 11.09.2015
 */

public class YandexTask {

	//�������� ������ � ���������� �������� ��� �������� �����
	private static List<String> data;
	private static List<SAClass> SA = new ArrayList<SAClass>();
	
	//��������� ������
	private static final int min_length = 4;
	/*
	 * ����������� ����� ���������� ��� ��������� ������� � �����.
	 * ���� ���������� = 2 ��� 3, �� ������ �������� ����������������, �� ���������� ������ ����� ������������,
	 * �.�. 2-3 ������� ������� ���� � ������ ��������� "��������", ��� �������� ������ �����
	 */
	
	private static final int acceptance_step = 4;
	/*
	 * ������ ��������� ���, ��� ��� ������� ��������� "������", ����������� �����, ��� ������ ����������� �����������
	 * ��� ��������� ��������� � ���� �����. ���� ���� �������� ����� 4, �� �� ������ 4 ������� "�����" ����� �����������
	 * ����������� � 1 ������. ��������, ����� "��������" ������ � ����� � ������ "�������" ��� "�������", �������� ��
	 * ������������ � 1 ������. ����� ����������� ���������, �.�. ����� "�������">4 � <9. ���� ��������� ���� ��������,
	 * �� ������ ����� ����� �������, �� ����� �������, ��������, ������ ������, ��� � ���� ������� �����, �.�.
	 * ������ ������� ������� ����� ���������.
	 */
	
	public static void main(String[] args) {
		
		long start_time = System.currentTimeMillis();
		String in;
		try {
			in = readFile("data.txt", Charset.defaultCharset());
			FileWriter out = new FileWriter("result.html", false);
			HTML.openDoc(out);
			
			data = new ArrayList<String>(Arrays.asList(in.split("\n")));
			
			//��������� ������ �� �������� ���������� �������� � ������
			Collections.sort(data, new Comparator<String>() {
				public int compare(String s1, String s2) {
					return s2.length() - s1.length();
				}
			});
			
			//������ ���������� �������
			for(int i=0; i<data.size(); i++){
				data.set(i, data.get(i).trim());
				SA.add(new SAClass(data.get(i).toLowerCase()));
			}
			
			//������ ����, ������� �� ������� ���������������� (��� ������� ��� ������� �������� �����)
			List<String> b = new ArrayList<String>(); 
	
			//total - �������� ���������� �������, k - ���������� ����������� ��� ������� � ����� (��. �������� acceptance_step)
			int total=0, k;
			
			String best_sub;
			/*
			 * best_sub - ���������, ����������� ����� �������, ��� ��� ����� ������, ���������� "������" best_sub,
			 * �� �������� "�����" �������, ��� best_sub. �� ����, ���� ���� 3 ������ "azzz", "abcd" � "abce", �� 
			 * best_sub ����������� ��� "abc", � �� "a". �������� �������� find_sub.
			 */
			
			while(data.size()>0){ //�������� ��������� = O((n^2)*(l^2)), �.�. find_sub ����������� �� O(n*(l^2))
				best_sub = find_sub(0, longestSub(0));
				int l = best_sub.length();
				if(l<min_length){
					b.add(data.get(0)); //��������� ������ � ������ ��������������������
					data.remove(0);
					SA.remove(0);
					continue;
				}
				
				total++;
				k=(l-1)/acceptance_step;
				
				//�������� ��� �������, �������� � ����� � ��������� "������" best_sub
				HTML.newTable(out);
				HTML.addTitle(out, best_sub);
				for(int i=0; i<data.size(); i++){
					if(longestSubstr(i, best_sub).length()>=(l-k)){
						HTML.addRow(out, data.get(i));
						data.remove(i);
						SA.remove(i);
						i--;
					}
				}
				HTML.closeTable(out);
			}
			
			HTML.newTable(out);
			HTML.addTitle(out, "Unclassified ("+total+" words)");
			for(int i=0; i<b.size();i++)
				HTML.addRow(out, b.get(i));
			HTML.closeTable(out);
			
			HTML.setModelParameters(out, total, b.size(), min_length, acceptance_step);
			HTML.closeDoc(out);
			out.close();
			
			System.out.println(total + " classes, " +b.size() + " words non-classified");
			System.out.println("Time: "+(System.currentTimeMillis()-start_time)+"ms");
		} catch (IOException e) {
			System.out.println("Can't read a file.");
			return;
		}
	}
	
	//������ ������� ���� ���������� ��������� ��� ������ data(k) �� ���� ������� d�ta. ��������� = O(n*l)
	public static String longestSub(int k){
		String longest = "";
		for(int i=0; i<data.size(); i++){
			if(i==k || data.get(i).length()<longest.length()) continue;
			String sub = longestSubstr(k,data.get(i));
			if(sub.length()>longest.length()) longest = sub;
		}
		return longest;
	}
	
	//����� "�����" ��� ��������� ������ ������. ��������� = O(n*(l^2))
	public static String find_sub(int k, String best_sub){
		int l = best_sub.length();
		for(int i=0; i<data.size();i++){
			if(i==k || data.get(i).length()<l) continue;
			String sub = longestSubstr(k, data.get(i));
			int t=0; //���������� ����������� ��� ��������� 2� ����
			if(l>acceptance_step) t = (l-1)/acceptance_step;
			if(longestSubstr(k, sub).length()>=l-t){ //����� ����� � ����� �� � ��������� �� ����������� t ����������
				String sub_longest = longestSub(i); //������� ����� ����������� "�����" ��� ���������� �����
				if(sub_longest.length()>best_sub.length()) //���� �� �������, ��� best_sub
					return find_sub(i, sub_longest); //�� ���������� ��������� ��� ���� ��������
				//� ������ ������� ��������, ����� best_sub ������������� ������� �� 1, �������������,
				//������� �������� ������ <=l, ��� l - ����� ������ �������� �����. ��� �������� ��������� ��������.
			}
		}
		return best_sub;
	}
	
	//����� ���������� ����� ��������� ����� ������� ����������� ���������� �������. ��������� = O(l)
	public static String longestSubstr(int first_index, String second) {
		return SA.get(first_index).lcs(second.toLowerCase());
	}
		
	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
