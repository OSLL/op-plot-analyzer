import com.opencsv.*;						//библиотека для чтения csv файла
import edu.stanford.nlp.process.*;			//библиотека для приведения слов в начальную форму Stemmer

import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
 
class OnPlotAnalyzer{
	private static String director = "";	//имя режиссера
	private static String filename = "";	//имя файла источника
	private static String genre = "";		//название жанра
	private static String country = "";		//название страны
	private static int mode = 0;			//режим работы программы:
						//режим 1:	--film_uniq_words		
						//			--director_uniq_words
						//			--country_uniq_words
						//			--genre_uniq_words
						//			сочетание режимов (director, country, genre)
						//режим 2:
						//режим 3:
	public static void main(String[] args) {
		mode = modeDetector(args); //определение режима выполнения программы
		if ((mode >= 1) && (mode <=3)){
			if (filename.equals("")){   //проверка на наличие названия файла
				System.out.println("The file name has not been set. Please restart the program and specify a source name in --filename argument.");
			}else{
				String reqName = requestHeadline(mode); //Название режима, которое будет выведено в консоль
				if(mode == 1){
					int lineCounter = 0;	//количество обработанных строк файла, нужно только для тестирования поведения программы
					int filmCounter = 0; 	//количество подошедших по запросу фильмов
					Map<String, Integer> uniqWords = new HashMap<String, Integer>();
					//Map<Character, Integer> uniqSymbols = new HashMap<Character, Integer>();		//Подсчет количества уникальных символов, тестовое значение	
					Stemmer stemm = new Stemmer();	//
					try (CSVReader csvReader = new CSVReader(new FileReader(filename));) {
							if ((csvReader.readNext()) != null){		//Чтение первой строки - заголовки столбцов обрабатываться не будут
					    		String[] values = null;					
					    		/**
					    		 * Пока обрабатываются только первые 12023 строки файла (одна треть)
					    		 * т к стандартные библиотеки (в частности opencsv) не читаю далее этот файл корректно
					    		 */
					    		while (((values = csvReader.readNext()) != null) && (lineCounter < 12023)) {
										boolean flag = true;			//Этот флаг определяет нужно ли обрабатывать для рейтинга текущую строку
										if (director != ""){
											flag = false;				//Если имя режиссера задано, но в строке его нет, то строка не будет обработана для рейтинга
											String[] allDirectors = columnParser(values[3]);	//Получение списка режиссеров в данной строке
											
											for (int i = 0; i < allDirectors.length; i++){
												if (Arrays.stream(allDirectors).anyMatch(director::equals)){flag = true;}
 											}
										}
										if (flag){		//Если все заданные параметры содержатся в строке, то она включается в рейтинг
											filmCounter++;
											String[] plotField = prepForStemmer(values[7]);	//Подготовка списка слов для обработки библиотекой Stemmer
											for (int j = 0; j<plotField.length; j++){		//Поочередная обработка слов
												String mstr = stemm.stem(plotField[j]);		

												if (uniqWords.keySet().contains(mstr)){
													int t = uniqWords.get(mstr);
													uniqWords.put(mstr, t + 1);
												}else{
													uniqWords.put(mstr, 1);
												}
												//Хранение списка уникальных символов слов
												/*for (int i = 0; i<mstr.length(); i++){
													if (uniqSymbols.keySet().contains(mstr.charAt(i))){
														int t = uniqSymbols.get(mstr.charAt(i));
														uniqSymbols.put(mstr.charAt(i), t+1);
													}else{
														uniqSymbols.put(mstr.charAt(i), 1);
													}
												}*/
											}
										}
									lineCounter++;
    							}
    						}		
					}catch(IOException e){}
					//Сортировка и вывод уникальных символов
        			/*LinkedList<Map.Entry<Character, Integer>> list = new LinkedList<>(uniqSymbols.entrySet());
					Comparator<Map.Entry<Character, Integer>> comparator = Comparator.comparing(Map.Entry::getValue);
					Collections.sort(list, comparator.reversed());
        			System.out.println(uniqSymbols);
        			for (int i = 0; i<list.size(); i++){
        				System.out.println(list.get(i));
        			}*/
        			
        			//Сортировка уникальных слов по убыванию убыванию популярности
        			LinkedList<Map.Entry<String, Integer>> listW = new LinkedList<>(uniqWords.entrySet());
					Comparator<Map.Entry<String, Integer>> comparatorW = Comparator.comparing(Map.Entry::getValue);
					Collections.sort(listW, comparatorW.reversed());
						
					System.out.println("\n" + reqName);
					System.out.println("Number of processed movies: " + filmCounter + "\n");
					for (int i = 0; i<listW.size(); i++){
        				System.out.println(listW.get(i));
        			}
        			
        			System.out.println("\n" + "Количество слов: " + uniqWords.size());
					//System.out.println("\n" + "Number of lines: " + counter);		//Количество обработанных строк файла записей		
				}
			}
		}else{
			System.out.println("Program mode not specified!");
		}
	}
	
	/** 
	 * Эта функция преобразует поле plot для корректной обработки библиотекой Stemmer.
	 */
	public static String[] prepForStemmer(String value){
	
		String str = value.replaceAll("n't", " not");
		str = str.replaceAll("'ll", " will");
		str = str.replaceAll("'d", " would");
		str = str.replaceAll("'ve", " have");
		str = str.replaceAll("'m", " am");
		str = str.replaceAll("'re", " are");
		
		str = str.replaceAll("' ", " ");
		str = str.replaceAll(" '", " ");
		str = str.replaceAll(" - ", " ");
		str = str.replaceAll("'s", "");
		str = str.replaceAll("’s", "");
		
		str = str.replaceAll("[^\\p{L}\\s\\-\\']", " ");		//Оставляет в строке только буквы различных алфавитов, тире и кавычки
		str = str.replaceAll("\\s+", " ");
		
		//StringBuilder использован для увеличения скорости работы метода toLowerCase
		StringBuilder plotField = new StringBuilder(str);
		for (int i = 0; i < plotField.length(); i++) {
   			char c = plotField.charAt(i);
   			plotField.setCharAt(i, Character.toLowerCase(c));
		}
		str = plotField.toString();
		String[] answer = str.split(" ");
		
		//Удаление кавычек в начале и конце слова
		for (int i = 0; i<answer.length; i++){
			Character ch = '\'';
			answer[i] = extraCharRemove(answer[i], ch);
		}
		return answer;
	}

	/**
	 * Удаляет из начала и конца слова лишний символ,
	 * который мог остаться в процессе разделения строки на слова.
	 */

	public static String extraCharRemove(String source, Character ch){
		if (source.length() > 1){
			if ((source.charAt((source.length())-1)) == ch){
				source = source.substring(0, source.length()-2);
			}
			if (source.length() > 1){
				if (source.charAt(0) == ch){
					source = source.substring(1, source.length()-1);
				}
			}else{
				if (source.equals(ch.toString())){
					source = "";
				}
			}
		}else{
			if (source.equals(ch.toString())){
				source = "";
			}
		}
		return source;
	}
	
	/**
	 * Разделяет входную строку с режиссерами на список режиссеров
	 * (например всех режиссеров или все страны).
	 */

	public static String[] columnParser(String source){			
		String str = source.replaceAll(" and ", ","); 		//Замена разделителей and и & на запятую для использования line.split(",");
		str = str.replaceAll(" & ", ","); 
		
		String[] dirExcepWords = {"II", ", Sr.", "Jr"};			//Список исключений, для корректного использования line.split(",");	
		for (int i = 0; i < dirExcepWords.length; i++){
			if (str.contains(", " + dirExcepWords[i])){
				str = str.replaceAll(", " + dirExcepWords[i], "# " + dirExcepWords[i]);
			}
		}
		String[] ans = str.split(",");
		
		//Удаление пробелов в начале и конце слова
		for (int i = 0; i<ans.length; i++){
			Character ch = ' ';
			ans[i] = extraCharRemove(ans[i], ch);
		}
		//После выполнения line.split(","); возвращаем на место запятые
		//не являющиеся разделителями.
		for (int i = 0; i < ans.length; i++){
			ans[i] = ans[i].replaceAll("#", ",");		
		}	
		return ans;
	}


	/**
	 * Определяет режим, в котором будет выполняться программа.
	 * режим 1:	--film_uniq_words		
	 *			--director_uniq_words
	 *			--country_uniq_words
	 *			--genre_uniq_words
	 *			сочетание режимов (director, country, genre)
	 * режим 2:
	 * режим 3:
	 */

	public static int modeDetector(String[] args){ 
		int i = 0;
		int mode = -1;
		
		while(i < args.length) {
            if (args[i].equals("-about")){
				System.out.println("This a kino analyser!");
			}
			if (args[i].equals("--director_uniq_words")){
				director = modeValuesParser(i+1, args, args[i]);
				if (director != ""){
					mode = 1;
				}
			}
			if (args[i].equals("--filename")){
				i++;
				filename = args[i];
			}
			i++;
        }
		return mode;
	}
	
	/**
	 * Считывание значения параметра из консоли
	 */

	public static String modeValuesParser (int i, String[] args, String regimeName){
		String inputValue = "";
		if (!((args[i].substring(0,1)).equals("-")) && !(i < args.length)){
			System.out.println("Parameter " + regimeName + " was not processed! It is empty!");
		}else{
			boolean flag = true;
			boolean nullWordFlag = true;			//Не дает добавить лишний пробел перед значением переменной
			while ((flag)&&(i<args.length)){
				if ((args[i].substring(0, 1)).equals("-")){		//Значение параметра считывается либо до конца входной строки либо до начала другого параметра (символ "-")
					flag = false;
				}else{
					if (!nullWordFlag){
						inputValue = inputValue + " ";
					}
					inputValue = inputValue + args[i];
					nullWordFlag = false;
					i++;
				}
			}
		}
		return inputValue;
	}
	
	/**
	 *Задает заголовок режима для вывода информации на экран
	 */
	 
	public static String requestHeadline (int mode){
		String reqName = "";
		if (mode == 1){
			reqName = "Rating of unique words in movies by:" + "\n";
			if (director != ""){
				reqName += "Director: " + director + "\n";
			}
		}
		return reqName;
	}


	
	/**
	 * Тестовая функция для определения уникальных составляющих массива строк.
	 * Используется для анализа прочитанных данных.
	 */
	public static void uniqComponents (String[] ans){			
																
		
		HashSet<String> uniqDirectors = new HashSet<String>();
		for (int i = 0; i < ans.length; i++){
			uniqDirectors.add(ans[i]);
 		}
        for (String s : uniqDirectors){
	    	System.out.println(s);
        }
		
	}
}

