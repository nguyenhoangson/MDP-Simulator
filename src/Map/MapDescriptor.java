package Map;

import java.util.ArrayList;
 
public class MapDescriptor {
    int length=20;
    int width=15;
    int[][] twoDMap;
    public MapDescriptor(){

    }
 /*  
    public static void main ( String[] args ){
        ArrayList<String> strings=new ArrayList<String>();
        MapDescriptor md=new  MapDescriptor();
        String s1="FFC07F80FF01FE03FFFFFFF3FFE7FFCFFF9C7F38FE71FCE3F87FF0FFE1FFC3FF87FF0E0E1C1F";
        String s2="00000100001C80000000001C0000080000060001C00000080000";  
        md.twoDMap=md.transMap(s1,s2);
        md.printMap(md.twoDMap);
        Map m=new Map(md.twoDMap,new int[]{0,0});
        
        strings=md.twoDtoStrings(md.twoDMap);
        System.out.println(s1);
        System.out.println((String)strings.get(0));
        System.out.println(s2);
        System.out.println(strings.get(1));
        
        
        
    }
    */ 
   //###########################################################################################   
   //1 and 2,5=>1, 0=>0, 3=>3 
    
    public int[][] transCurMap(int[][] curMap){
          int[][] tCM=new int[curMap.length][curMap[0].length];
          
          for(int i=0;i<curMap.length;i++){
            for(int j=0;j<curMap[0].length;j++){
				//if(curMap[i][j]!=0&&curMap[i][j]!=3){
				if(curMap[i][j]!=3){   
                    tCM[i][j]=1;
                    
                }
                else{
                   tCM[i][j]=curMap[i][j];
                }
            }
          }
          
    
    
          return tCM;
    }
    
    
 //###########################################################################################   
 //transform 2D Arrays to Map description strings
    public ArrayList<String> twoDtoStrings(int[][] twoDMap){
         ArrayList<String> strings=new ArrayList<String>();
       
         String s1=generateS1(twoDMap);
        
         //System.out.print(s1);
         
         String s2=generateS2(twoDMap);
         //System.out.print(s1);
         strings.add(s1);
         strings.add(s2);
          
         return strings;   
    }
    
    
    public String generateS1(int[][] twoDMap){
        
        String s1="";
        String temp="11";
        String t="";
        char a;
        
        int explored=0;
        int[][] copyMap=new int[twoDMap.length][twoDMap[0].length];
        
        for(int i=0;i<twoDMap.length;i++){
            for(int j=0;j<twoDMap[0].length;j++){
                copyMap[i][j]=twoDMap[i][j];   //0,1,3
                if(copyMap[i][j]!=0){  //explored,1or 3
                    explored++;
                    if(copyMap[i][j]==3) copyMap[i][j]=1;
                }
            }
        }
        
        
        
        for(int i=0;i<copyMap.length;i++){
            for(int j=0;j<copyMap[0].length;j++){
                
                temp=temp+Integer.toString(copyMap[i][j]);
            
            }
            
        }
        temp=temp+"11";
        
       
        
        
        for(int m=0;m<304/4;m++){
            String sub=temp.substring(m*4,m*4+4);
            t=Integer.toHexString(Integer.parseInt(sub, 2));
            a=t.charAt(0);
            
            if(a=='a'||a=='b'||a=='c'||a=='d'||a=='e'||a=='f'){
                t=t.toUpperCase();
                
            }    
            s1+=t;
        }
        
        
        return s1;
    
    }
    
    
    public String generateS2(int[][] twoDMap){
        String s2="";
        String t="";
        char a;
        String temp="";
        
        for(int i=0;i<twoDMap.length;i++){
            for(int j=0;j<twoDMap[0].length;j++){
                if(twoDMap[i][j]!=0){
                    
                    if(twoDMap[i][j]==1){
                        temp+="0";
                    }
                    else{temp+='1';}
                
                }
                
                
            }
        }
        
        while(temp.length()%4!=0){
            temp+="0";
        
        }
        
        
        for(int m=0;m<temp.length()/4;m++){
            String sub=temp.substring(m*4,m*4+4);
            t=Integer.toHexString(Integer.parseInt(sub, 2));
            a=t.charAt(0);
            
            if(a=='a'||a=='b'||a=='c'||a=='d'||a=='e'||a=='f'){
                t=t.toUpperCase();
                
            }    
            s2+=t;
        }
        
        return s2;
        
    }
    
 //###########################################################################################   
 //transform Map description strings to 2D Arrays
    public int[][] stringsto2D(String s1, String s2){
        int[][] twoDMap=transMap(s1,s2);
        
        return twoDMap;
    
        
    }
   
   //String s1="FFC07F80FF01FE03FFFFFFF3FFE7FFCFFF9C7F38FE71FCE3F87FF0FFE1FFC3FF87FF0E0E1C1F";
   //String s2="00000100001C80000000001C0000080000060001C00000080000";  
    
    public int[][] transMap(String s1,String s2){
        int[][] occupancy=new int[this.length][this.width];
        
        int m=s1.length();
        int n=s2.length();
        
        int[] t1=new int[304];  //15*20+2+2
        int[] t2=new int[n*4];
        
        int explored=0;
        
        t1=transArray(s1);
        t2=transArray(s2);

         
        for(int k=0;k<length*width;k++){
          int i=k/width;
          int j=k%width;
          occupancy[i][j]=t1[2+k];
          if(occupancy[i][j]==1) explored++;
        
        }
        
        //System.out.print(explored);
        
        int p=0;
        
        for(int i=0;i<length;i++){
        
            for(int j=0;j<width;j++){
                if(p<explored){
                    if(occupancy[i][j]==1){//explored

                        if(t2[p]==1) occupancy[i][j]=3; //obstable
                        p++;
                    }
            
                }
            }
            
            
        }
         
         
        
         return occupancy;
        
    }
    
    
    private int[] transArray(String s){    
        String temp;
        int[] t=new int[s.length()*4];
        int m=s.length();
        
        for(int k=0;k<m;k++){
            
            String sub=s.substring(k,k+1);
            //System.out.println(sub);
            temp=Integer.toBinaryString(Integer.valueOf(sub,16));
            while(temp.length()<4){
                temp="0"+temp;
            }
            //System.out.println(temp);
            
           for(int j=0;j<4;j++){
                t[k*4+j]=Integer.parseInt(String.valueOf(temp.charAt(j)));
                 //System.out.println(t[k*4+j]);
            }
        }
        
        return t;
    }
    
    
  //###########################################################################################    
     public void printMap(int[][] map){
        
        for(int i=0;i<map.length;i++){
            System.out.print("{");
            for(int j=0;j<map[0].length;j++){
                if(j==(map[0].length-1)){
                    System.out.print(map[i][j]);
                }
                else{System.out.print(map[i][j]+",");}
            }
        
             System.out.println("},");
        }
       
    
    }
    
    
}
