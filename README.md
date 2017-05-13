# Tweets-Clustering-Using-K-Means
cluster tweets by utilizing Jaccard Distance metric and K-means clustering algorithm

Created by Xiangru Zhou
*************************************************
Twitter provides a service for posting short messages. In practice, many of the tweets are very similar to each other and can be clustered together. By clustering similar tweets together, we can generate a more concise and organized representation of the raw tweets, which will be very useful for many Twitter-based applications (e.g., truth discovery, trend analysis, search ranking, etc.)

In this project, you will learn how to cluster tweets by utilizing Jaccard Distance metric and K-means clustering algorithm.

******************************************

Objectives:

Compute the similarity between tweets using the Jaccard Distance metric. 

Cluster tweets using the K-means clustering algorithm.

*************************************************

Introduction to Jaccard Distance:

The Jaccard distance, which measures dissimilarity between two sample sets (A and B). It is defined as the difference of the sizes of the union and the intersection of two sets divided by the size of the union of the sets.

In this project, a tweet can be considered as an unordered set of words such as {a,b,c}. By "unordered", we mean that {a,b,c}={b,a,c}={a,c,b}=...

A Jaccard Distance Dist(A, B) between tweet A and B has the following properties:

It is small if tweet A and B are similar.

It is large if they are not similar.

It is 0 if they are the same.

It is 1 if they are completely different (i.e., no overlapping words).

Here is the reference for more details about Jaccard Distance: http://en.wikipedia.org/wiki/Jaccard_index

Note that the tweets do not have the numerical coordinates in Euclidean space, you might want to think of a sensible way to compute the "centroid" of a tweet cluster. This could be the tweet having minimum distance to all of the other tweets in a cluster.

********************************
Inputs to your K-means Algorithm:

The number of clusters K (default to K=25).

The tweet dataset is in JSON format.

The list of initial centroids are given.

Note that each element in this list is the tweet ID (i.e., the id field in JSON format) of the tweet in the dataset.

***************************************
Validation:

The usual method of evaluating the goodness of clustering will be used. It is the Sum of Squared Error (SSE) function.
