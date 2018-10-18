package uk.ac.cam.bz267.Algorithms.Tick3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.HashSet;
import java.util.HashMap;

public class Graph extends GraphBase{
  public Graph(URL url) throws IOException {
    super(url);
  }
  public Graph(String file) throws IOException {
		super(file);
	}
  public Graph(int adj[][]){
    super(adj);
  }
  public int Min(int a, int b){
    if (a>b){return b;}
    else {return a;}
  }
  public static int flowVal(int [][] flow, int s){
    int flowval = 0;
    int sss = flow.length;
    for (int i = 0; i < sss; i++){
      if (flow[s][i] > 0){
        flowval = flowval + flow[s][i];
      }
      else if (flow[i][s] > 0){
        flowval = flowval - flow[i][s];
      }
    }
    return flowval;
  }
  public static int[][] AntiPar (int[][] mat){
    int l = mat.length;
    int nA = 0;
    HashMap<Integer, Integer> aa= new HashMap<>();
    for (int i = 0; i < l; i++){
      for (int j = i; j < l; j++){
        if ((mat[i][j] != 0) && (mat[j][i] != 0)){
          nA ++ ;
          aa.put(i, j);
        }
      }
    }
    int newmat [] [] = new int[l+nA][l+nA];
    for (int i = 0; i < l; i++){
      for (int j = 0; j < l; j++){
        newmat[i][j] = mat[i][j];
      }
    }
    if (nA > 0){
      int next = l;
      for (Integer k : aa.keySet()){
        newmat[k][next] = mat[k][aa.get(k)];
        newmat[next][aa.get(k)] = mat[k][aa.get(k)];
        newmat[k][aa.get(k)] = 0;
        next ++ ;
      }
    }
    return newmat;
  }
  protected int [][] hg = AntiPar(mAdj);
  protected int[][] mFlow = new int[hg.length][hg.length];
  public static int[][] cleanUp(int[][] mat, int l){
    int size1 = mat.length;
    int to = 0; int from = 0; int val = 0;
    if (size1 > l){
      for (int i = l; i < size1; i ++){
        for (int c = 0; c < l; c++){
          if (mat[l][c] != 0){
            val = mat[l][c];
            to = c;
          }
          else if (mat[c][l] != 0){
            from  = c;
          }
        }
        mat[from][to] = val;
      }
      int[][] arr= new int [l][l];
      for (int i = 0; i < l; i++){
        for (int j = 0; j < l; j++){
          arr[i][j] = mat[i][j];
        }
      }
      return arr;
    }
    else {
      return mat;
    }
  }
  @Override
  public List<Integer> getFewestEdgesPath(int src, int target) throws TargetUnreachable{
    Queue<Integer> toexplore = new LinkedList<>();
    LinkedList<Integer> path = new LinkedList<>();
    int[] isReachable = new int[hg.length];
    int[] comefrom = new int[hg.length];
    int[] seen = new int[hg.length];
    toexplore.add(src);
    while (!toexplore.isEmpty()){
      Integer v = toexplore.element();
      for (int i = hg.length-1; i > 0; i--){
        if (((hg[v][i] - mFlow[v][i] != 0) || (mFlow[i][v] != 0)) && (seen[i] == 0)){
          toexplore.add(i);
          seen[i] = 1;
          comefrom[i] = v;
          isReachable[i] = 1;
        }
      }
      toexplore.remove();
    }
      if (isReachable[target] == 0) {
        throw new TargetUnreachable();
      }
      else{
        path.addFirst(target);
        while (path.element() != src){
          path.addFirst(comefrom[path.element()]);
        }
        }
      return path;
    }
    @Override
    public MaxFlowNetwork getMaxFlow(int s, int t) {
      int sss = hg.length;
      mFlow = new int[sss][sss];
      while (true){
        int[][] rg = hg.clone();
        for (int i=0; i<sss; i++) {
          rg[i]=hg[i].clone();
        }
        for (int i = 0; i < sss; i++){
          for (int j = 0; j < sss; j++){
            rg[i][j] = rg[i][j] - mFlow[i][j];
          }
        }
        Graph RG = new Graph(rg);
        try{
          List<Integer> path = RG.getFewestEdgesPath(s, t);
          int l = path.size();
          int delta = 32767;
          for (int i = 0; i < l-1; i++){
            if (rg[path.get(i)][path.get(i+1)] > 0){
              delta = Min(delta, hg[path.get(i)][path.get(i+1)] - mFlow[path.get(i)][path.get(i+1)]);
            }
            else if (rg[path.get(i+1)][path.get(i)] > 0){
              delta = Min(delta, mFlow[path.get(i+1)][path.get(i)]);
            }
          }
          for (int i = 0; i < l-1; i++){
            if (rg[path.get(i)][path.get(i+1)] > 0){
              mFlow[path.get(i)][path.get(i+1)] = mFlow[path.get(i)][path.get(i+1)] + delta;
            }
            else if (rg[path.get(i+1)][path.get(i)] > 0){
              mFlow[path.get(i+1)][path.get(i)] = mFlow[path.get(i+1)][path.get(i)] - delta;
            }
          }
        }
        catch(TargetUnreachable e){ break; }
      }
      int[][] flow1 = cleanUp(mFlow, mN);
      MaxFlowNetwork mf1 = new MaxFlowNetwork(flowVal(flow1, s), new Graph(flow1));
      return mf1;
  }
    public static void main(String [] args){
      int mAdj[][] = new int[4][4];
      mAdj[0][1] = 12;
      mAdj[1][0] = 12;
      mAdj[0][2] = 7;
      mAdj[1][2] = 15;
      mAdj[1][3] = 10;
      mAdj[2][3] = 10;
      /*int[][] ap = AntiPar(mAdj);
      for (int i = 0; i < ap.length; i++){
        for (int j = 0; j < ap.length; j++){
          System.out.print(ap[i][j] + " ");
        }
        System.out.println();
      }
      int[][] ap1 = cleanUp(ap, mAdj.length);
      for (int i = 0; i < mAdj.length; i++){
        for (int j = 0; j < mAdj.length; j++){
          System.out.print(ap1[i][j] + " ");
        }
        System.out.println();
      }*/
      Graph g1 = new Graph(mAdj);
      MaxFlowNetwork mf1 = g1.getMaxFlow(0, 3);
      System.out.println(mf1.getFlow());
    }
}
