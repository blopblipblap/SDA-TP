import java.io.*;
import java.util.*;

public class LadangBoba {
    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);
    
    public static void main(String[] args) {

      //Bagian sort (rencana saya memakai quicksort tetapi tidak jadi) dan proses query dibantu oleh teman saya Asfiolitha Wilmarani / 1906350944
      //Code untuk Input dan Output saya ambil dari template Lab1.java
        
        //Ladang
        int N = in.nextInt();   //Banyak ladang
        
        //Banyak biji boba di ladang ke-i
        int banyakBoba[][] = new int[N+1][N+1];
        int tempLadang[] = new int[N];
        int jumlah[] = new int[N];
        for (int ai = 0; ai < N; ai++) { //O(N)
            int biji = in.nextInt();
            tempLadang[ai] = biji;
        }
        banyakBoba[0] = tempLadang;
        //Keranjang
        int M = in.nextInt();   //Banyak keranjang ajaib
        
        //Detail keranjang
        Map<String, Keranjang> listKeranjang2 = new HashMap<String, Keranjang>();
        Keranjang krj[] = new Keranjang[M];
        for (int a = 0; a < M; a++) {   //O(2N^2)
            String s = in.next();
            int c = in.nextInt();
            int f = in.nextInt();
            Keranjang newKeranjang = new Keranjang(s, c, f, 0);
            krj[a] = newKeranjang;
            listKeranjang2.put(s, newKeranjang);
            panenBoba(newKeranjang, N, banyakBoba, jumlah); //O(2N)
        }
        
        sortKeranjang(krj);
        getFirstDay(krj);
        
      //Input Query
      //Query kondisi
        Deque<Object[]> pelanggan = new ArrayDeque<Object[]>();
        Deque<String> orangnya = new ArrayDeque<String>();
        int H = in.nextInt();   //Lamanya hari ladang bisa dipanen
        int Y = 0; //Variable jumlah pelanggan
        int O = 0; //Variable batas pelanggan
        
        //Izuri only territory
        String sIzuri = "";
        int cIzuri = 0;
        int fIzuri = 0;
        String oldSIzuri = "";
        String newSIzuri = "";
        for (int anjay = 0; anjay < H-1; anjay++) { //O(N^2)
            String QIzuri = in.next();
            if (QIzuri.equalsIgnoreCase("ADD")) {
                sIzuri = in.next();
                cIzuri = in.nextInt();
                fIzuri = in.nextInt();
            } else if (QIzuri.equalsIgnoreCase("SELL")) {
                sIzuri = in.next();
            } else if (QIzuri.equalsIgnoreCase("UPDATE")) {
                sIzuri = in.next();
                cIzuri = in.nextInt();
                fIzuri = in.nextInt();
            } else if(QIzuri.equalsIgnoreCase("RENAME")) {
                oldSIzuri = in.next();
                newSIzuri = in.next();
            } else {
                break;
            }
            
            //Pelanggan
            Y = in.nextInt(); //Banyak pelanggan
            if (Y > 0) {
                for (int y = 0; y < Y; y++) { //O(N)
                    String orang = in.next();
                    String Q2 = in.next();
                    if (Q2.equalsIgnoreCase("ADD")) {
                        String s2 = in.next();
                        int c2 = in.nextInt();
                        int f2 = in.nextInt();
                        Object[] inputs = new Object[] {(String)orang, (String)Q2, (String)s2, (int)c2, (int)f2};
                        pelanggan.offer(inputs); //add ke deque
                    } else if (Q2.equalsIgnoreCase("SELL")) {
                        String s2 = in.next();
                        Object[] inputs = new Object[] {(String)orang, (String)Q2, (String)s2};
                        pelanggan.offer(inputs); //add ke deque
                    } else if (Q2.equalsIgnoreCase("UPDATE")) {
                        String s2 = in.next();
                        int c2 = in.nextInt();
                        int f2 = in.nextInt();
                        Object[] inputs = new Object[] {(String)orang, (String)Q2, (String)s2, (int)c2, (int)f2};
                        pelanggan.offer(inputs); //add ke deque
                    } else if(Q2.equalsIgnoreCase("RENAME")) {
                        String oldS = in.next();
                        String newS = in.next();
                        Object[] inputs = new Object[] {(String)orang, (String)Q2, (String)oldS, (String)newS};
                        pelanggan.offer(inputs); //add ke deque
                    } else {
                        break;
                    }
                }
            }
            //Proses Pelanggan
            O = in.nextInt(); //Batas pelanggan
            if (O > 0) {
                for (int o = 0; o < O; o++) { //O(N)
                    Object[] temp = pelanggan.pop();
                    orangnya.add((String)temp[0]);
                    if (((String)temp[1]).equalsIgnoreCase("ADD")) {
                        String s2 = (String)temp[2];
                        int c2 = (int)temp[3];
                        int f2 = (int)temp[4];
                        if (!listKeranjang2.containsKey(s2)) {
                            Keranjang oke = new Keranjang(s2, c2, f2, 0);
                            listKeranjang2.put(s2, oke);
                            panenBoba(oke, N, banyakBoba, jumlah);
                        }
                    } else if(((String)temp[1]).equalsIgnoreCase("SELL")) {
                        String s2 = (String)temp[2];
                        if (listKeranjang2.containsKey(s2)) {
                            listKeranjang2.remove(s2);
                        }
                    } else if(((String)temp[1]).equalsIgnoreCase("UPDATE")) {
                        String s2 = (String)temp[2];
                        int c2 = (int)temp[3];
                        int f2 = (int)temp[4];
                        if (listKeranjang2.containsKey(s2)) {
                            Keranjang oke = listKeranjang2.get(s2);
                            oke.setC(c2);
                            oke.setF(f2);
                            panenBoba(oke, N, banyakBoba, jumlah);
                        }
                    } else {
                        String oldS = (String)temp[2];
                        String newS = (String)temp[3];
                        if (!listKeranjang2.containsKey(newS)) {
                            Keranjang oke = listKeranjang2.get(oldS);
                            if (oke != null) {
                                oke.setS(newS);
                                listKeranjang2.remove(oldS);
                                listKeranjang2.put(newS, oke);
                            }
                        }
                    }
                }
            }
            //Proses Izuri
            if (QIzuri.equalsIgnoreCase("ADD")) {
                if (!listKeranjang2.containsKey(sIzuri)) {
                    Keranjang oke = new Keranjang(sIzuri, cIzuri, fIzuri, 0);
                    listKeranjang2.put(sIzuri, oke);
                    panenBoba(oke, N, banyakBoba, jumlah);
                }
            } else if (QIzuri.equalsIgnoreCase("SELL")) {
                if (listKeranjang2.containsKey(sIzuri)) {
                    listKeranjang2.remove(sIzuri);
                }
            } else if(QIzuri.equalsIgnoreCase("UPDATE")) {
                if (listKeranjang2.containsKey(sIzuri)) {
                    Keranjang oke = listKeranjang2.get(sIzuri);
                    oke.setC(cIzuri);
                    oke.setF(fIzuri);
                    panenBoba(oke, N, banyakBoba, jumlah);
                }
            } else {
                if (!listKeranjang2.containsKey(newSIzuri)) {
                    Keranjang oke = listKeranjang2.get(oldSIzuri);
                    if (oke != null) {
                        oke.setS(newSIzuri);
                        listKeranjang2.remove(oldSIzuri);
                        listKeranjang2.put(newSIzuri, oke);
                    }
                } else if (oldSIzuri.equals(newSIzuri)) {
                    continue;
                }
            }
            Keranjang krj2[] = new Keranjang[listKeranjang2.size()]; //menampung value dari hashmap
            int idxArray = 0;
            for (Map.Entry<String, Keranjang> oho : listKeranjang2.entrySet()) { //O(N)
                krj2[idxArray] = oho.getValue();
                idxArray++;
            }
            if (krj2.length > 1) {
                sortKeranjang(krj2); //worst case O(N^2)
            }
           getOutput(krj2, (anjay+2), orangnya);
        }
        out.close();
    }
  
    static class Keranjang {
        private String S;
        private int C;
        private int F;
        private int boba;
        
        public Keranjang(String S, int C, int F, int boba) {
            this.S = S;
            this.C = C;
            this.F = F;
            this.boba = boba;
        }
        
        public void setS(String newS) {
            this.S = newS;
        }
        
        public void setC(int newC) {
            this.C = newC;
        }
        
        public void setF (int newF) {
            this.F = newF;
        }
        
        public void setBoba (int newBoba) {
            this.boba = newBoba;
        }
        
        public String getS() {
            return this.S;
        }
        
        public int getC() {
            return this.C;
        }
        
        public int getF() {
            return this.F;
        }
        
        public int getBoba() {
            return this.boba;
        }
        
        public String toString() {
            return String.format(this.S);
        }
        
        public int compareTo(Keranjang other) {
            if (this.boba == other.boba) {
               int compare = (this.S).compareTo(other.S);
               if  (compare < 0) {return 2;}
               else {return -2;}
            } else {
                if (this.boba > other.boba) {return -1;}
                else {return 1;}
            }
        }
    }
    
    static void panenBoba(Keranjang keranjang, int N, int banyakBoba[][], int jumlah[]) { 
        //Proses panen Boba
        //Saya mendapat ide algoritma dari teman saya,
        //Nabila Khansa 1906293221
        //M. Fathan Muthahhari 1906293190
        
        //Hitung maks per keranjang
        int fKeranjang = keranjang.getF();
        int tempBooba = 0;
        int cKeranjang = 0;
        for (int i = 1; i <= N; i++) { //O(N^2)
            cKeranjang = (keranjang.getC())+((i-1)*fKeranjang);
            for (int a = 1; a <= N; a++) { //O(N)
                if (i == 1) {
                    banyakBoba[i][a] = banyakBoba[i][a-1] + banyakBoba[i-1][a-1];
                    if (banyakBoba[i][a] > cKeranjang) {
                        banyakBoba[i][a] = cKeranjang;
                    }
                    tempBooba = banyakBoba[i][a];
                }
                else {
                    if (a < i) {
                        banyakBoba[i][a] = 0;
                    } else {
                        banyakBoba[i][a] = banyakBoba[i][a-1] + banyakBoba[0][a-1];
                        if (banyakBoba[i][a] > cKeranjang) {
                            banyakBoba[i][a] = cKeranjang;
                            tempBooba = banyakBoba[i][a];
                        } else if (banyakBoba[i][a] < banyakBoba[i-1][a-1]) {
                            banyakBoba[i][a] = banyakBoba[i-1][a-1];
                            tempBooba = banyakBoba[i][a];
                        } else {
                            tempBooba = banyakBoba[i][a];
                        }
                    }
                }
            }
            jumlah[i-1] = tempBooba;
            tempBooba = 0;
        }
        
        //Hitung maxBoba untuk keranjang yang dimaksud
        int maxBoba = 0;
        for (int a = 0; a < jumlah.length; a++) { //O(N)
            if (jumlah[a] > cKeranjang) {
                if (cKeranjang > maxBoba) {
                    maxBoba = cKeranjang;
                }
             } else {
                if (jumlah[a] > maxBoba) {
                    maxBoba = jumlah[a];
                }
              }
         }
         keranjang.setBoba(maxBoba);
         maxBoba = 0;
    }
    
    static void sortKeranjang(Keranjang[] a) { //Proses sort keranjang (Bubble Sort)
        for (int i = a.length-1; i>=0; i--) { //worst case O(N^2)
            boolean swapped = false;
            for (int j = 0; j < i; j++) {
                if (a[j].boba < a[j+1].boba) {
                    Keranjang temp = a[j];
                    a[j] = a[j+1];
                    a[j+1] = temp;
                    swapped = true;
                } else if (a[j].boba == a[j+1].boba) {
                    int hasil = (a[j].S).compareTo(a[j+1].S);
                    if (hasil > 0) {
                        Keranjang temp = a[j];
                        a[j] = a[j+1];
                        a[j+1] = temp;
                        swapped = true;
                    }
                }
            }
            if (!swapped)
                return;
        }
    }
    
    static void getFirstDay(Keranjang[] listKeranjang) { //output untuk hari pertama
        out.write("Hari ke-1:\nHasil Panen");
        for (Keranjang keranjangnya : listKeranjang) {
            out.write("\n"+keranjangnya.getS()+" "+keranjangnya.getBoba());
        }
        out.write("\n\n");
    }
    
    static void getOutput(Keranjang[] listKeranjang, int a, Deque<String> orangnya) { //output untuk hari kedua dan seterusnya
        out.write("Hari ke-"+a+":\nPermintaan yang dilayani\n");
        for (String orangan : orangnya) {
            out.write(orangan+" ");
            orangnya.pop();
        }
        out.write("IZURI\nHasil Panen");
        for (Keranjang keranjangnya : listKeranjang) {
            out.write("\n"+keranjangnya.getS()+" "+keranjangnya.getBoba());
        }
        out.write("\n\n");
    }
    
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;
 
        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }
 
        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }
 
        public int nextInt() {
            return Integer.parseInt(next());
        }
 
    }
}