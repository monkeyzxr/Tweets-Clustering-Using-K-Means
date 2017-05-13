import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.*;

public class TweetsCluster {	
	public static void main(String [] agrs){
		String command,command1;
		String initialCentroid, inputFile, outputFile;
		System.out.println("Input command like this: <K-Value><InitialCentroidsFileName><TextFileName><OutputFileName> :"
				+ "\n[Here is an example input: <25><InitialCentroids.txt><Tweets.json><results.txt>]");			
		Scanner s = new Scanner(System.in);
		command1 = s.nextLine().replace("<", "");
		command = command1.replaceAll(">", "\t");
		String [] commandSplit = command.split("\t");

		initialCentroid = commandSplit[1];
		inputFile = commandSplit[2];		
		outputFile = "/workspace_Java/ML-Assignment5-Part2/src"+ commandSplit[3];					
		ArrayList<String []> texts=DataImport.getText(inputFile);								
		ArrayList<String> id=DataImport.getID(inputFile);
		ArrayList<Integer> centroidIndex=DataImport.getSeedsIndex(id, initialCentroid);
		HashMap<Integer, ArrayList<Integer>> clusterResult=new HashMap<>();
		
		int iterations=0;
		do {
			ArrayList<String []> centroidText = getCentroidTexts(centroidIndex, texts);
			HashMap<Integer, ArrayList<Integer>> currentCluster = getCluster(centroidText, texts);
			ArrayList<Integer> currentNewCentroids = getNewCentroids(currentCluster, texts, id);			
			clusterResult = currentCluster;			
			if(compareCneters(currentNewCentroids, centroidIndex)) break;			
			centroidIndex = currentNewCentroids;
			iterations = iterations + 1;			
		} while(iterations < 25);
		
		for(Map.Entry<Integer, ArrayList<Integer>> e : clusterResult.entrySet()){			
			ArrayList<String> idFinalResult=getIDsOfPoints(e.getValue(), id);			
			System.out.print(e.getKey()+1+":\t");
			int i = 0;
			while(i < idFinalResult.size()){				
				if(i == idFinalResult.size()-1)
					System.out.print(idFinalResult.get(i));
				else
					System.out.print(idFinalResult.get(i) + ", ");
				
				i++;
			}	
			System.out.println(" ");			
		}
		
		Double sse = calculateSSE(clusterResult, texts, centroidIndex);
		System.out.println("==================================");
		System.out.println("SSE is : "+sse);

		try{
			PrintWriter writer=new PrintWriter(outputFile);						
			for(Map.Entry<Integer, ArrayList<Integer>> e : clusterResult.entrySet()){								
			    ArrayList<String> idFinalResult = getIDsOfPoints(e.getValue(), id);	
			    writer.print(e.getKey()+1+":\t");
			    int i = 0;
			    while(i < idFinalResult.size()){
				    if(i == idFinalResult.size()-1)
					    writer.print(idFinalResult.get(i));
				    else
					    writer.print(idFinalResult.get(i)+", ");			    
				    i++;
			    }
			    writer.println(" ");			
			}
			writer.println("===================================");
			writer.println("SSE is : "+sse);
		}
		catch(Exception e){
			//System.out.println("Error. Cannot Output.");		
		}			
	}

	
	public static Integer getCentroidIndex(ArrayList<String []> centroidTexts, String [] location){				
		ArrayList<Double> dist =new ArrayList<>();	
		Integer indexResult = 0;
		for(int i = 0; i < centroidTexts.size(); i++){
			String [] currentTest = centroidTexts.get(i);			
			dist.add(calculateDistance(location, currentTest));
		}		
		Double current = dist.get(0);		
		for(int i = 0; i < dist.size(); i++){
			Double currentDistance = dist.get(i);			
			if(currentDistance.compareTo(current) <0 ){
				indexResult = i;
				current=dist.get(i);
			}
		}		
		return indexResult;
	}
	
	public static ArrayList<String []> getCentroidTexts(ArrayList<Integer> centroidIndex, ArrayList<String []> location){
		ArrayList<String []> result=new ArrayList<>();
		int i = 0;
		while(i < centroidIndex.size()){
			String [] current=location.get(centroidIndex.get(i));
			result.add(current);			
			i++;
		}		
		return result;
	}
	
	
	public static HashMap<Integer, ArrayList<Integer>> getCluster(ArrayList<String []> centroidText, ArrayList<String []> location){		
		HashMap<Integer, ArrayList<Integer>> result=new HashMap<>();		
		for(int i=0; i<location.size(); i++){
			String [] point=location.get(i);
			Integer currentIn=getCentroidIndex(centroidText, point);		
			if(result.containsKey(currentIn)){
				ArrayList<Integer> currentCluster=result.get(currentIn);
				currentCluster.add(i);
				result.replace(currentIn, currentCluster);
			}
			else{
				ArrayList<Integer> newCluster=new ArrayList<>();
				newCluster.add(i);
				result.put(currentIn, newCluster);
			}
		}		
		return result;		
	}
	
	public static ArrayList<Integer> getNewCentroids(HashMap<Integer, ArrayList<Integer>> cluster, ArrayList<String []> pointsText, ArrayList<String> ids){
		ArrayList<Integer> newCenters=new ArrayList<>();		
		for(Map.Entry<Integer, ArrayList<Integer>> e : cluster.entrySet()){
			HashMap<Integer, Double> IndexAndDistance=new HashMap<>();			
			ArrayList<String []> TextInCluster=new ArrayList<>();			
			ArrayList<Integer> PointsInCurrentCluster=e.getValue();			
			for(int a=0; a < PointsInCurrentCluster.size(); a++){
				String[] currentText=pointsText.get(PointsInCurrentCluster.get(a));			
				TextInCluster.add(currentText);
			}
			
			Double currentDist=0.0;
			Integer tempIndexResult=0;
			for(int k=0; k<PointsInCurrentCluster.size(); k++){
				String [] CurrentPointText=pointsText.get(PointsInCurrentCluster.get(k));				
				IndexAndDistance.put(PointsInCurrentCluster.get(k), getFinalDistance(CurrentPointText, TextInCluster));				
				currentDist=getFinalDistance(CurrentPointText, TextInCluster);
				tempIndexResult=PointsInCurrentCluster.get(k);
			}					
			
			for(Map.Entry<Integer,Double> e1 : IndexAndDistance.entrySet()){
				if(e1.getValue().compareTo(currentDist)<0){
					tempIndexResult=e1.getKey();
					currentDist=e1.getValue();
				}
			}								
			newCenters.add(tempIndexResult);			
		}				
		return newCenters;
	}

	public static ArrayList<String> getIDsOfPoints(ArrayList<Integer> indexes, ArrayList<String> IDs){
		ArrayList<String> ID=new ArrayList<>();	
		int i = 0;
		while(i < indexes.size()){
			ID.add(IDs.get(indexes.get(i)));			
			i++;
		}		
		return ID;
	}
	
	
	public static Double calculateSSE(HashMap<Integer, ArrayList<Integer>> clusterResult, ArrayList<String []> texts,ArrayList<Integer> centroidIndex){		
		double result=0.0;
		for(Map.Entry<Integer, ArrayList<Integer>> entry : clusterResult.entrySet()){
			String [] currentCenterTexts=texts.get(centroidIndex.get(entry.getKey()));			
			ArrayList<Integer> pointsIndexInACluster=entry.getValue();
			ArrayList<String []> pointsTexts=getCentroidTexts(pointsIndexInACluster, texts);			
			for(int i=0; i<pointsTexts.size();i++){
				String [] point=pointsTexts.get(i);
				double squareResult=Math.pow(calculateDistance(point, currentCenterTexts), 2);					
				result = result + squareResult;
			}
		}				
		return result;			
	}

	
	public static double calculateDistance(String [] x, String [] y){				
		ArrayList<String> X=new ArrayList<>();
		ArrayList<String> Y=new ArrayList<>();
		ArrayList<String> inCommon=new ArrayList<>();	
		for(int i = 0; i<x.length; i++){
			if(!X.contains(x[i]))
				X.add(x[i]);
		}		
		for(int i=0; i<y.length; i++){
			if(!Y.contains(y[i]))
				Y.add(y[i]);
		}		
		for(int i=0; i<X.size(); i++){
			for(int j=0; j<Y.size(); j++){
				if(X.get(i).equals(Y.get(j)))
					inCommon.add(X.get(i));				
			}
		}		
		double numInCommon=(double)inCommon.size();
		double xSize=(double)X.size();
		double ySize=(double)Y.size();
		double result= 1 - numInCommon/(xSize+ySize-numInCommon);
		return result;
	} 
	
	
	public static double getFinalDistance(String [] x, ArrayList<String[]> cluster){
		Double finalDist=0.0;		
		for(int i=0; i<cluster.size(); i++){
			String [] currentPoint=cluster.get(i);		
			finalDist = finalDist + calculateDistance(x, currentPoint);
		}		
		return finalDist;
	}

	
	public static boolean compareCneters(ArrayList<Integer> previousCenter, ArrayList<Integer> newCenter){
		boolean result = true;
		if(previousCenter.size()!=newCenter.size())
			return false;
		else{			
			for(int i=0; i<previousCenter.size();i++){
				if(previousCenter.get(i).compareTo(newCenter.get(i)) != 0)
					return false;
			}
		}
		return result;
	}
	
	
}


class DataImport{		
	public static ArrayList<Integer> getSeedsIndex(ArrayList<String> IDs, String seedsFile){
		ArrayList<Integer> Index=new ArrayList<>();
		ArrayList<String> seeds=importSeeds(seedsFile);	
		int i =0;
		while(i < seeds.size()){
			Index.add(IDs.indexOf(seeds.get(i)));
			i++;
		}		
		return Index;
	}

	public static ArrayList<String> importData(String fileName){		
		BufferedReader reader=null;
		ArrayList<String> dataset = new ArrayList<>();		
		try{
			String readerLine="";
			reader = new BufferedReader(new InputStreamReader(DataImport.class.getResourceAsStream(fileName)));			
			while((readerLine = reader.readLine())!= null)
				dataset.add(readerLine);			
		}
		catch(Exception e){			
			System.out.println("Error In Data Import");
		}		
	    return dataset;
	}
	
	public static ArrayList<String> importSeeds(String seedsFile){
		BufferedReader reader=null;
		ArrayList<String> dataset = new ArrayList<>();
		try{
			String readerLine="";
			reader = new BufferedReader(new InputStreamReader(DataImport.class.getResourceAsStream(seedsFile)));
			while((readerLine = reader.readLine())!= null)			
			    dataset.add(readerLine.replaceAll(",", ""));						
		}
		catch(Exception e){
			System.out.println("Error In Data Import");	
		}	
		return dataset;
	}
	
	public static ArrayList<String []> getText(String inputFile){
		ArrayList<String> dataset=importData(inputFile);		
		ArrayList<String []> text=new ArrayList<>();
		for(int i=0; i<dataset.size();i++){ 			
	        Pattern pattern = Pattern.compile("text\":\\s\"(.*?)\",\\s\"profile_image_url");
	        Matcher matcher = pattern.matcher(dataset.get(i));        
	        while (matcher.find()) {	        	
	    	    String [] tempText=matcher.group(1).split("\\s+");
	    	    text.add(tempText);
	        }
		}		
		return text;
	}
	
		
	public static ArrayList<String> getID(String inputFile){
		ArrayList<String> dataset=importData(inputFile);			
		ArrayList<String> id=new ArrayList<>();
		for(int i=0; i<dataset.size();i++){   
	        Pattern patternNew = Pattern.compile("\"id\":\\s(.*?),\\s\"iso_language_code\"");
	        Matcher matcherNew = patternNew.matcher(dataset.get(i));
	        while (matcherNew.find()) 
	    	    id.add(matcherNew.group(1).replace(",", ""));	    
		}		
		return id;
	}
		
}

