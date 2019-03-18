import com.opencsv.*;
//import edu.stanford.nlp.*;
import edu.stanford.nlp.process.*;

import java.io.*;
//import java.io.FileReader;
//import java.io.IOException;

import java.util.*;
//import java.io.File;
//import java.io.FileNotFoundException;
 
class PlotSymbAnalyser{
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

	public static void main(String[] args) throws Exception{
		mode = modeDetector(args); //определение режима выполнения программы
		if ((mode >= 1) && (mode <=3)){
			if (filename.equals("")){   //проверка на наличие названия файла
				System.out.println("The file name has not been set. Please restart the program and specify a source name in --filename argument.");
			}else{
				if(mode == 1){
					int counter = 0;	//количество обработанных строк файла, нужно только для тестирования поведения программы
					int normalLines = 0;
					int badLines = 0;
	//				SortedSet<String> mySet1 = new TreeSet<String>();
//					ArrayList<Integer> list = new ArrayList<Integer>();
					HashSet<String> uniqDirectors = new HashSet<String>();
					HashSet<String> uniqOrigin = new HashSet<String>();
					Map<Character, Integer> uniqSymbols = new HashMap<Character, Integer>();
					int counter3 = 0;
					
					
					//Stemmer stemm = new Stemmer();
					//System.out.print(stemm.stem("class怪es"));
       				//System.out.print(' ');
					
					FileWriter fout = new FileWriter("plotsField12024.txt");
					
					try (CSVReader csvReader = new CSVReader(new FileReader(filename));) {
							String temp[];
							if ((temp = csvReader.readNext()) != null){		//Чтение первой строки - заголовки столбцов обрабатываться не будут
					    		String[] values = null;
					    		while (((values = csvReader.readNext()) != null) && (counter < 12024)) {

										boolean flag = true;
										if (director != ""){
											//String[] allDirectors = columnParser(values[3]);
											
											
											/*if (allDirectors.length > 1){
												//System.out.println(ans[j] + "             ");
												System.out.println(values[3]);
												counter3++;
											}*/
											
											boolean localFlag = false;
											//for (int i = 0; i < allDirectors.length; i++){
												//if (allDirectors[i].contains("Jr.")){ //&& (allDirectors[i].length() == 3)){
													//System.out.println(values[3]);
													//System.out.println(allDirectors[i]);
												//}
												//if (Arrays.stream(allDirectors).anyMatch(director::equals)){localFlag = true;}
												//uniqDirectors.add(allDirectors[i]);
 											//}
 											//if (!localFlag){flag = false;}
										}
										if (flag){
											//System.out.println(values[3]);
											for (int i = 0; i<values[7].length(); i++){
												String mstr = values[7].toLowerCase();
												//mstr = mstr.toLowerCase();
												fout.write(mstr.charAt(i));
												if (uniqSymbols.keySet().contains(mstr.charAt(i))){
													
											//uniqSymbols.put('t', 1);
											//uniqSymbols.put('w', 5); 
													int t = uniqSymbols.get(mstr.charAt(i));
													//System.out.println(t);
													uniqSymbols.put(mstr.charAt(i), t+1);
												}else{
													//uniqSymbols.put('t', 1);
													//uniqSymbols.put('w', 5);
													uniqSymbols.put(mstr.charAt(i), 1);
												}
												//uniqSymbols.add(mstr.charAt(i));
											}
											
											//uniqSymbols.put('t', 1);
											//uniqSymbols.put('w', 5);
										}
										//uniqOrigin.add(values[2]);
									   
										flag = true; 
									
									counter++;
    							}
    						}		
					}catch(IOException e){}
					fout.close();
					int dirCounter = 0;
					/*System.out.println("------------------------------------------");
					for (Character s : uniqSymbols){
						//if (s.length() < 7){
	    					System.out.println(s);
	    				//}
	    				dirCounter++;
        			}
        			System.out.println("------------------------------------------");
        			System.out.println("Films: " + counter3);
        			System.out.println("Количество уникальных слов: " + dirCounter);*/
        			
        			/*int oriCounter = 0;
					for (String s : uniqOrigin){
						//if (s.length() < 7){
	    					System.out.println(s);
	    				//}
	    				oriCounter++;
        			}*/
        			LinkedList<Map.Entry<Character, Integer>> list = new LinkedList<>(uniqSymbols.entrySet());
					Comparator<Map.Entry<Character, Integer>> comparator = Comparator.comparing(Map.Entry::getValue);
					Collections.sort(list, comparator.reversed());
        			//System.out.println(uniqSymbols);
        			for (int i = 0; i<list.size(); i++){
        				System.out.println(list.get(i) + "    i = " + i);
        			}
        			
        		
        			System.out.println("Количество уникальных символов: " + list.size());
					System.out.println("");
					System.out.println("Number of lines: " + counter);		//Количество выведенных записей
			
				}
			}
		}else{
			//System.out.println("Program mode not specified!");
		}
		
	}


	public static String[] columnParser(String source){			//////функция выделяет все составляющие строки в массив составляющих
		String str = source.replaceAll(" and ", ","); 			//Заменям разделитель строки and на запятую для удобства парсера
		str = str.replaceAll(" & ", ","); 
		
		String[] dirExcepWords = {"II", ", Sr.", "Jr"};			//Список исключений, для корректного использования line.split(",");
		//for (int i = 0; i < dirExcepWords.length; i++){dirExcepWords[i] = ", " + dirExcepWords[i];}		
		for (int i = 0; i < dirExcepWords.length; i++){
			if (str.contains(", " + dirExcepWords[i])){
				str = str.replaceAll(", " + dirExcepWords[i], "# " + dirExcepWords[i]);
			}
		}
		String[] ans = str.split(", ");
		for (int i = 0; i < ans.length; i++){
			ans[i] = ans[i].replaceAll("#", ",");		
		}
		/*if (ans.length > 1){
			System.out.println("");
		}*/
		/*for (int j = 0; j < ans.length; j++){
			if (ans.length > 1){
				//System.out.println(ans[j] + "             ");
				System.out.println(source);
			}
		}*/
		
		
		
		return ans;
	}
	
	public static void uniqComponents (String[] ans){			//Тестовая функция для определения уникальных составляющих массива строк
																//Используется для анализа прочитанных данных
		
		HashSet<String> uniqDirectors = new HashSet<String>();
		for (int i = 0; i < ans.length; i++){
			uniqDirectors.add(ans[i]);
 		}
        for (String s : uniqDirectors){
	    	System.out.println(s);
        }
		
	}

	public static int modeDetector(String[] args){ //метод определяет режим, в котором будет выполняться программа
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

	public static String modeValuesParser (int i, String[] args, String regimeName){		//Считывание значения параметра из консоли
		String inputValue = "";
		if (!((args[i].substring(0,1)).equals("-")) && !(i < args.length)){
			System.out.println("Parameter " + regimeName + " was not processed! It is empty!");
		}else{
			boolean flag = true;
			boolean nullWordFlag = true;		//Не дает добавить лишний пробел перед значением переменной
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
}




