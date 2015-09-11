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
 * Автор: Фролов Дмитрий
 * Дата: 11.09.2015
 */

public class YandexTask {

	//Исходные данные и суффиксные автоматы для исходных строк
	private static List<String> data;
	private static List<SAClass> SA = new ArrayList<SAClass>();
	
	//Параметры модели
	private static final int min_length = 4;
	/*
	 * Минимальная длина совпадения для включения объекта в класс.
	 * Если установить = 2 или 3, то больше объектов классифицируется, но результаты станут менее релевантными,
	 * т.к. 2-3 символа гораздо чаще в словах совпадают "случайно", чем являются корнем слова
	 */
	
	private static final int acceptance_step = 4;
	/*
	 * Модель построена так, что чем длиннее найденный "корень", порождающий класс, тем больше допускается погрешность
	 * для включения элементов в этот класс. Если этот параметр равен 4, то на каждые 4 символа "корня" будет допускаться
	 * погрешность в 1 символ. Например, слово "Липецкий" войдет в класс с корнем "ЛипецкА" или "ЛипецкИ", несмотря на
	 * несовпадение в 1 символ. Такая погрешность допустима, т.к. длина "Липецка">4 и <9. Если увеличить этот параметр,
	 * то классы будут более точными, но самих классов, очевидно, станет больше, что в свою очередь плохо, т.к.
	 * многие похожие объекты будут разделены.
	 */
	
	public static void main(String[] args) {
		
		long start_time = System.currentTimeMillis();
		String in;
		try {
			in = readFile("data.txt", Charset.defaultCharset());
			FileWriter out = new FileWriter("result.html", false);
			HTML.openDoc(out);
			
			data = new ArrayList<String>(Arrays.asList(in.split("\n")));
			
			//Сортируем массив по убыванию количества символов в строке
			Collections.sort(data, new Comparator<String>() {
				public int compare(String s1, String s2) {
					return s2.length() - s1.length();
				}
			});
			
			//Строим суффиксные деревья
			for(int i=0; i<data.size(); i++){
				data.set(i, data.get(i).trim());
				SA.add(new SAClass(data.get(i).toLowerCase()));
			}
			
			//Список слов, которые не удалось классифицировать (нет похожих или слишком короткие слова)
			List<String> b = new ArrayList<String>(); 
	
			//total - итоговое количество классов, k - допустимая погрешность для допуска в класс (см. описание acceptance_step)
			int total=0, k;
			
			String best_sub;
			/*
			 * best_sub - подстрока, построенная таким образом, что все слова списка, содержащие "корень" best_sub,
			 * не содержат "корня" длиннее, чем best_sub. То есть, если есть 3 строки "azzz", "abcd" и "abce", то 
			 * best_sub определится как "abc", а не "a". Строится функцией find_sub.
			 */
			
			while(data.size()>0){ //Итоговая сложность = O((n^2)*(l^2)), т.к. find_sub выполняется за O(n*(l^2))
				best_sub = find_sub(0, longestSub(0));
				int l = best_sub.length();
				if(l<min_length){
					b.add(data.get(0)); //Добавляем строку в список неклассифицированных
					data.remove(0);
					SA.remove(0);
					continue;
				}
				
				total++;
				k=(l-1)/acceptance_step;
				
				//Отбираем все объекты, вошедшие в класс с найденным "корнем" best_sub
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
	
	//Данная функция ищет наибольшую подстроку для строки data(k) во всем массиве dаta. Сложность = O(n*l)
	public static String longestSub(int k){
		String longest = "";
		for(int i=0; i<data.size(); i++){
			if(i==k || data.get(i).length()<longest.length()) continue;
			String sub = longestSubstr(k,data.get(i));
			if(sub.length()>longest.length()) longest = sub;
		}
		return longest;
	}
	
	//Поиск "корня" для выделения нового класса. Сложность = O(n*(l^2))
	public static String find_sub(int k, String best_sub){
		int l = best_sub.length();
		for(int i=0; i<data.size();i++){
			if(i==k || data.get(i).length()<l) continue;
			String sub = longestSubstr(k, data.get(i));
			int t=0; //допустимая погрешность при сравнении 2х слов
			if(l>acceptance_step) t = (l-1)/acceptance_step;
			if(longestSubstr(k, sub).length()>=l-t){ //Нашли слово с такой же с точностью до погрешности t подстрокой
				String sub_longest = longestSub(i); //Смотрим длину наибольшего "корня" для найденного слова
				if(sub_longest.length()>best_sub.length()) //Если он длиннее, чем best_sub
					return find_sub(i, sub_longest); //То рекурсивно запускаем для него алгоритм
				//С каждым уровнем рекурсии, длина best_sub увеличивается минимум на 1, следовательно,
				//глубина рекурсии всегда <=l, где l - длина самого большого слова. Это поясняет указанную скорость.
			}
		}
		return best_sub;
	}
	
	//Поиск наибольшей общей подстроки через заранее построенный суффиксный автомат. Сложность = O(l)
	public static String longestSubstr(int first_index, String second) {
		return SA.get(first_index).lcs(second.toLowerCase());
	}
		
	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
