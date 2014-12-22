package Database;

import java.io.File;
import java.util.List;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ItemBasedRecommender {
	public List<RecommendedItem> Recommend() throws Exception{
		long startTime=System.currentTimeMillis(); 
    	DataModel model = new FileDataModel(new File("output2.csv"));
    	ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);    	
        GenericBooleanPrefItemBasedRecommender recommender = new GenericBooleanPrefItemBasedRecommender(model,  similarity);
    	List<RecommendedItem> recommendations = recommender.recommend(9999, 5);
    	long endTime=System.currentTimeMillis();
    	System.out.println("Item-Based Recommender(GenericBooleanPrefItemBasedRecommender/EuclideanDistanceSimilarity)");
    	System.out.println("Recommend for user 9999:");
    	System.out.println("Time:"+(endTime-startTime));
    	return recommendations;
    /*	for (RecommendedItem recommendation : recommendations) {
    	  System.out.println(recommendation);
    	}*/
    }
}
