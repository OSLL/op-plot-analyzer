import com.opencsv.*;						//библиотека для чтения csv файла
import edu.stanford.nlp.process.*;			//библиотека для приведения слов в начальную форму Stemmer
import picocli.CommandLine;					//библиотека picocli v3.9.5 для обработки параметров командной строки
import picocli.CommandLine.*;

import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

@Command(name = "OnPlotAnalyzer", header = "%n@|green Utility help|@")
class OnPlotAnalyzer implements Runnable{
	private static String director = "";	//имя режиссера
	private static String title = "";		//название фильма
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

	//Чтение параметров командной строки
	@Option(names = {"-about"}, usageHelp = true, description = "Help menu")
    private boolean help;
    
	@Option(names = {"--director_uniq_words"}, arity = "1..*", description = "Director name")
    void setDirector(String dir[]) {
    	this.mode = 1;
    	for (int i = 0; i< dir.length; i++){
	    	if (i != 0){this.director += " ";}
    		this.director += dir[i];
    	}
    }
	
	@Option(names={"-f", "--filename"}, description="Path and name of file", required=true)
	void setFilename(String filename) { this.filename = filename;}
	
	@Option(names = {"--film_uniq_words"}, arity = "1..*", description = "Movie title")
    void setTitle(String mov[]) {
    	this.mode = 1;
    	for (int i = 0; i< mov.length; i++){
    		if (i != 0){this.title += " ";}
    		this.title += mov[i];
    	}
    }
    	
    @Option(names = {"--genre_uniq_words"}, arity = "1..*", description = "Movie genre")
    void setGenre(String gen[]) {
		this.mode = 1;
    	for (int i = 0; i< gen.length; i++){
    		if (i != 0){this.genre += " ";}
    		this.genre += gen[i];}
	}
	

						
	public static void filmsRating(CSVReader csvReader, String reqName){
	
	}
	
	public static void similarDirectorsRating(CSVReader csvReader, String reqName){
	
	}
	
	/**
	 *	Запускает режим выполнения программы в соответствии со значением mode
	 */
	
	public static void modeDistributor(CSVReader csvReader) throws IOException{
		String reqName = requestHeadline(mode); //Название режима, которое будет выведено в консоль
		switch (mode){
			case 1:	wordsRating(csvReader, reqName);
					break;
			case 2: filmsRating(csvReader, reqName);
					break;
			case 3: similarDirectorsRating(csvReader, reqName);
					break;
			default: System.out.println("Wrong mode! Please check your input!");
					break;
		}
	}
	
	/** 
	 * Работы утилиты в режиме 1. Осуществляется при указании режимов:	
	 *		--film_uniq_words		
	 *		--director_uniq_words
	 *		--country_uniq_words
	 *		--genre_uniq_words
	 *		сочетание режимов (director, country, genre)
	 */
	
	public static void wordsRating(CSVReader csvReader, String reqName) throws IOException{
		int lineCounter = 0;	//количество обработанных строк файла, нужно только для тестирования поведения программы
		int filmCounter = 0; 	//количество подошедших по запросу фильмов
		Map<String, Integer> uniqWords = new HashMap<String, Integer>();
		
		String[] values = null;
		Stemmer stemm = new Stemmer();
					
		/**
		 * Пока обрабатываются только первые 12023 строки файла (одна треть)
		 * т к стандартные библиотеки (в частности opencsv) не читаю далее этот файл корректно
		 */
		while (((values = csvReader.readNext()) != null) && (lineCounter < 12023)) {
	  		if (values.length != 8){throw new IOException("Wrong CSV format, number of columns must be 8!");}
			boolean flag = true;			//Этот флаг определяет нужно ли обрабатывать для рейтинга текущую строку
			if (title != ""){
				flag = false;				
				if (title.equals(values[1])){flag = true;}
			}else{
				if (director != ""){
					flag = false;				//Если имя режиссера задано, но в строке его нет, то строка не будет обработана для рейтинга
					String[] allDirectors = columnParser(values[3]);	//Получение списка режиссеров в данной строке
											
					for (int i = 0; i < allDirectors.length; i++){
						if (Arrays.stream(allDirectors).anyMatch(director::equals)){flag = true;}
 					}
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

				}
			}
			lineCounter++;
    	}

    	wordsRatingOut(uniqWords, reqName, filmCounter);
	}
	
	/**
	 *	Выводит на экран результат работы в режиме 1 (mode=1).
	 *	Режим выводит рейтинг частоты употребления слов,
	 *	в соответствии с заданными критериями.
	 */
	
	public static void wordsRatingOut(Map<String, Integer> uniqWords, String reqName, int filmCounter){
        			
        //Сортировка уникальных слов по убыванию убыванию популярности
        LinkedList<Map.Entry<String, Integer>> list = new LinkedList<>(uniqWords.entrySet());
		Comparator<Map.Entry<String, Integer>> comparator = Comparator.comparing(Map.Entry::getValue);
		Collections.sort(list, comparator.reversed());
					
		System.out.println("\n" + reqName);
		System.out.println("Number of processed movies: " + filmCounter + "\n");
		for (int i = 0; i<list.size(); i++){
        	System.out.println(list.get(i));
        }
        			
        System.out.println("\n" + "Words number: " + uniqWords.size());
		//System.out.println("\n" + "Number of lines: " + counter);		//Количество обработанных строк файла записей
	}
	
	
	/**
	 *	Организация чтения файла.
	 */
	
	public static void csvFileReader (){
		try (CSVReader csvReader = new CSVReader(new FileReader(filename));) {
			if ((csvReader.readNext()) != null){		//Чтение первой строки - заголовки столбцов обрабатываться не будут
				modeDistributor(csvReader);
    		}		
		}catch(IOException e){
			System.out.println("Error! Can't parse file " + filename + "!");
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args){
		CommandLine.run(new OnPlotAnalyzer(), args);
	}
	
	/**
	 *	Запускает обработку параметров командной строки и саму утилиту.
	 */
	
	public void run(){
		if ((mode >= 1) && (mode <=3)){
			csvFileReader();
		}else{
			System.out.println("Program mode not specified!");
		}
	}
	
	
	/**
	 *	Заполнение мапы с исключениями. Исключения помогают 
	 *  корректнее разбивать текст на отдельные слова.
	 */
	 
	 public static Map<String, String> plotExcepListGenerator(){
	 	Map<String, String> excepList = new HashMap<String, String>();

	 	excepList.put("n't", " not");
		excepList.put("'ll", " will");
		excepList.put("'d", " would");
		excepList.put("'ve", " have");
		excepList.put("'m", " am");
		excepList.put("'re", " are");
		
		excepList.put("' ", " ");
		excepList.put(" '", " ");
		excepList.put(" - ", " ");
		excepList.put("'s", "");
		excepList.put("’s", "");
		
		excepList.put("[^\\p{L}\\s\\-\\']", " ");		//Оставляет в строке только буквы различных алфавитов, тире и кавычки
		excepList.put("\\s+", " ");
		
		return excepList;
	 }
	
	/** 
	 * Эта функция преобразует поле plot для корректной обработки библиотекой Stemmer.
	 */
	public static String[] prepForStemmer(String value){
		Map<String, String> excepList = new HashMap<String, String>();
		excepList = plotExcepListGenerator();			/*Получение актуальных данных для обработки исключений
														при разделении строки поля plot на слова*/
		String str = value;
		for (Map.Entry<String, String> entry : excepList.entrySet()) {
			str = str.replaceAll(entry.getKey(), entry.getValue());         
    	}
		
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
	 *Задает заголовок режима для вывода информации на экран
	 */
	 
	public static String requestHeadline (int mode){
		String reqName = "";
		if (mode == 1){
			reqName = "Rating of unique words in movies by:" + "\n";
			if (title != ""){
				reqName += "Movie title: " + title + "\n";
			}
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

