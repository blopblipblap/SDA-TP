import java.io.*;
import java.util.*;
import java.math.*;

//Ref Graph:
//https://www.geeksforgeeks.org/implementing-generic-graph-in-java/#:~:text=The%20Graph%20Class%20is%20implemented,unweighted%20graph%20with%205%20vertices.
//https://algorithms.tutorialhorizon.com/weighted-graph-implementation-java/

//Ref DFS:
//https://www.geeksforgeeks.org/depth-first-search-or-dfs-for-a-graph/
//https://www.programiz.com/dsa/graph-dfs

//Ref Binary Search:
//https://www.geeksforgeeks.org/binary-search/

//Ide tanyaKupon dibantu oleh teman saya Hasiana Emanuela R/1906293083
//Ide untuk memakai Binary Search dibantu oleh teman saya Asfiolitha Wilmarani / 1906350944

public class OdadingIzuri {
    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);

    public static void main(String[] args) {
        int N = in.nextInt(); //Banyaknya toko pada kota Naga
        int M = in.nextInt(); //Banyaknya ruas jalan biasa
        int E = in.nextInt(); //Banyaknya ruas jalan eksklusif
        int Q = in.nextInt(); //Banyaknya pertanyaan
        
        WeightedGraph graph = new WeightedGraph();
        
        //Nama Toko
        for (int n = 0; n < N; n++) { //O(N)
            String namaToko = in.next();
            graph.addVertex(namaToko);
        }
        
        //Ruas jalan biasa
        for (int m = 0; m < M; m++) { //O(M)
            String A = in.next(); //Asal
            String B = in.next(); //Tujuan
            int C = in.nextInt(); //Waktu tempuh
            int K = in.nextInt(); //Kupon
            int T = in.nextInt(); //Waktu tutup
            graph.addEdge(A, B, C, K, T, false);
        }
        
        //Ruas jalan eksklusif
        for (int e = 0; e < E; e++) { //O(E)
            String F =in.next();  //Asal
            String G = in.next(); //Tujuan
            int L = in.nextInt(); //Kupon
            int U = in.nextInt(); //Waktu Tutup
            graph.addEdge(F, G, 1, L, U, true);
        }
        
        graph.bikinAdj();
        
        //Pertanyaan
        for (int q = 0; q < Q; q++) { //O(Q)
            String query = in.next();
            //Tanya Jalan
            if (query.equalsIgnoreCase("TANYA_JALAN")) {
                int X = in.nextInt();
                out.println(graph.tanyaJalan(X));
            //Tanya Hubung
            } else if (query.equalsIgnoreCase("TANYA_HUBUNG")) {
                String s1 = in.next();
                String s2 = in.next();
                boolean hasil = graph.tanyaHubung(s1, s2);
                if (hasil == true) {
                    out.println("YA");
                } else {
                    out.println("TIDAK");
                }
            //Tanya Kupon
            } else if (query.equalsIgnoreCase("TANYA_KUPON")) {
                String s1 = in.next();
                String s2 = in.next();
                out.println(graph.tanyaKupon(s2, s1));
            //Tanya Ex
            } else if (query.equalsIgnoreCase("TANYA_EX")) {
                String s1 = in.next();
                String s2 = in.next();
                out.println(graph.tanyaEx(s2, s1, 0, 100000));
            //Tanya Biasa
            } else {
                String s1 = in.next();
                String s2 = in.next();
                out.println(graph.tanyaBiasa(s2, s1, 0, 100000));
            }
        }
        //graph.printlah();
        out.close();
    }

    static class WeightedGraph {
        //Class untuk Edge
        class Edge {
            String A; //Vertex asal
            String B; //Vertex tujuan
            int C; //Waktu Tempuh
            int K; //Kupon
            int T; //Ditutup
            boolean khusus; //biasa = false, khusus = true
            
            public Edge(String A, String B, int C, int K, int T, boolean khusus) {
                this.A = A;
                this.B = B;
                this.C = C;
                this.K = K;
                this.T = T;
                this.khusus = khusus;
            }
            
            public String toString() {
                return String.format(this.A+" "+this.B+" "+this.C+" "+this.K+" "+this.T+" "+this.khusus);
            }
        }
        
        //Class untuk Vertex
        class Vertex implements Comparator<Vertex>{
            String vertex; //Nama toko
            int jarak; //Cost/jarak
            
            public Vertex() {
                
            }
            
            public Vertex(String vertex, int jarak) {
                this.vertex = vertex;
                this.jarak = jarak;
                
            }
            
            @Override
            public int compare(Vertex vertex1, Vertex vertex2) {
                if (vertex1.jarak < vertex2.jarak)
                    return -1;
                if (vertex1.jarak > vertex2.jarak)
                    return 1;
                return 0;
            }
        }
        
        //Adjacency List
        private Map<String, List<Edge>> adjacencyList = new HashMap<>(); //Adjacency List
        private List<Edge> edge = new ArrayList<Edge>(); //List khusus semua edge
        private List<Integer> kupon = new ArrayList<Integer>(); //List khusus semua kupon
        private Map<String, Integer> adj = new HashMap<>(); //List pembagian index untuk setiap toko
        private int dist[]; //Array distance untuk dijkstra
        private Set<String> hijau; //Set untuk store dijkstra
        private PriorityQueue<Vertex> pq; //Priority Queue untuk dijkstra
        
        //Method penambahan vertex(toko)    
        public void addVertex(String s) {
            adjacencyList.put(s, new LinkedList<Edge>());
        }
            
        //Method penambahan edge/ruas jalan
        public void addEdge(String A, String B, int C, int K, int T, boolean khusus) {
            Edge edgeA = new Edge(A, B, C, K, T, khusus);
            Edge edgeB = new Edge(B, A, C, K, T, khusus);
            adjacencyList.get(A).add(edgeA);
            adjacencyList.get(B).add(edgeB);
            edge.add(edgeA); //add ke list "edge"
            edge.add(edgeB);
            kupon.add(K); //add ke list "kupon"
        }
        
        //Method untuk print graph beserta adjacencynya
        public void printlah() {
            for (String v : adjacencyList.keySet()) {
                out.println(v + " : ");
                for (Edge e : adjacencyList.get(v)) {
                    out.println(e.A + "-->" + e.B);
                    out.println("Waktu: " + e.C);
                    out.println("Kupon: " + e.K);
                    out.println("Tutup: " + e.T);
                }
            }
        }
        
        //Method membuat index untuk toko
        public void bikinAdj() {
            int count = 0;
            for (String s : adjacencyList.keySet()) { //O(M+E)
                adj.put(s, count);
                count++;
            }
        }
        
        ////Method untuk TANYA_JALAN
        public int tanyaJalan(int X) {
            int count = 0;
            for (Edge e : edge) { //O(M+E)
                if (e.T > X) {
                    count++;
                }
            }
            return count/2;
        }
        
        //
        //Method untuk TANYA_HUBUNG
        //
        
        //Inisiasi awal
        public boolean tanyaHubung(String asal, String tujuan) {
            //Jika adjacency list node asal kosong
            if (adjacencyList.get(asal).isEmpty()) {
                return false;
            } else {
                //List == visit sequence
                List<String> list = DFS(asal, tujuan);
                if (list.contains(tujuan)) { //Jika tujuan ada di list
                    return true;
                } else { //Jika tidak ada di list
                    return false;
                }
            }
        }
        
        //Method DFS
        List<String> DFS(String asal, String tujuan) {
            boolean visited[] = new boolean[adjacencyList.size()];
            List<String> list = new ArrayList<String>();
            list.add(asal); //add toko asal ke visit sequence(list)
            DFSRec(adj.get(asal), asal, tujuan, visited, list);
            return list;
        }
        
        void DFSRec(int a, String asal, String tujuan, boolean visited[], List<String> list) {
            visited[a] = true;
            for (Edge x : adjacencyList.get(asal)) { //O(M+E)
                String edge = x.B;
                int v = adj.get(edge);
                if (!visited[v]) {
                    list.add(edge);
                    DFSRec(v, edge, tujuan, visited, list);
                }
            }
        }
        
        //
        //Method untuk TANYA_KUPON
        //
        
        //Method utama
        public BigInteger tanyaKupon(String asal, String tujuan) {
            dist = new int[adjacencyList.size()]; //Array of distance
            hijau = new HashSet<String>(); //"Awan Hijau"
            pq = new PriorityQueue<Vertex>(adjacencyList.size(), new Vertex()); //"Awan abu-abu"
            
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            int base = 0;
            //Cari max untuk testing base untuk kupon
            for (int a : kupon) {
                if (a > max) {
                    max = a;
                }
                if (a < min) {
                    min = a;
                }
            }
            //Jika semua kupon = 1
            if (max == 1 && min == 1) {
                base = 1;
            }
            
            //Search base
            for (int i = 2; i <= max; i++) {
                double temp = Math.log(max) / Math.log(i);
                double log = Math.round(temp*10000)/10000.0;
                if (log % 1 == 0) {
                    base = i;
                    break;
                }
            }
            
            //Inisialisasi array dist menjadi max value
            for (int i = 0; i < adjacencyList.size(); i++) {
                dist[i] = Integer.MAX_VALUE;
            }
            
            //Add asal ke priority queue
            pq.add(new Vertex(asal, 0));

            //Jarak dari asal ke asal diganti jadi 0
            dist[adj.get(asal)] = 0;
            while(!pq.isEmpty()) { //O(M^2+E^2+ME)
                
                //remove vertex dengan distance terkecil
                String v = pq.remove().vertex;
                
                //Masukan ke "awan hijau"
                hijau.add(v);
                
                dijkstraKupon(v, asal, base);
            }
            
            //Pangkat yang dihasilkan dari dijkstra
            int hasil = dist[adj.get(tujuan)];
            BigInteger akhir = BigInteger.valueOf(0);
            if (hasil == Integer.MAX_VALUE) {
                 akhir = BigInteger.valueOf(-1);
            } else {
                BigInteger baseBig = BigInteger.valueOf((long) base);
                BigInteger hasilPangkat = baseBig.pow(hasil);
                akhir = hasilPangkat.mod(new BigInteger("1000000007"));
            }
            return akhir;
        }
        
        //Dijkstra untuk kupon
        private void dijkstraKupon(String v, String asal, int base) {
            int edgeDistance = -1;
            int newDistance = -1;
            for (int i = 0; i < adjacencyList.get(v).size(); i++) { //O(M+E)
                Edge e = adjacencyList.get(v).get(i); //Get edge/ruas jalan
                if (!hijau.contains(e.B)) {
                    if (base == 1) { 
                        edgeDistance = 1;
                    } else {
                        double temp = (Math.log(e.K) / Math.log(base));
                        edgeDistance = (int) (Math.round(temp*100)/100.0);
                    }
                    //newDistance = hasil penambahan pangkat sebelum dan sekarang
                    newDistance = dist[adj.get(e.A)] + edgeDistance;
                    if (newDistance < dist[adj.get(e.B)]) { //Jika <<
                        dist[adj.get(e.B)] = newDistance;
                    }
                    //Add ke "awan abu-abu"
                    pq.add(new Vertex(e.B, dist[adj.get(e.B)]));
                }
            }
        }
        
        //
        //Method untuk TANYA_BIASA
        //
        
        //Method utama
        public int tanyaBiasa(String asal, String tujuan, int l, int r) {
            dist = new int[adjacencyList.size()];
            hijau = new HashSet<String>();
            pq = new PriorityQueue<Vertex>(adjacencyList.size(), new Vertex());
            
            for (int i = 0; i < adjacencyList.size(); i++) {
                dist[i] = Integer.MAX_VALUE;
            }
            
            //Proses Binary Search
            //Base Case
            if (r < l) {
                return r;
            }

            int mid = (l+r)/2;
            bantuanBiasa(asal, tujuan, mid);
            
            //Jika Izuri "gagal" sampai ke tujuan, geser ke kiri
            if (dist[adj.get(tujuan)] == Integer.MAX_VALUE) {
                return tanyaBiasa(asal, tujuan, l, mid-1);
            //Else: geser ke kanan
            } else {
                return tanyaBiasa(asal, tujuan, mid + 1, r);
            }
        }
        
        //Method bantuan untuk dijkstra
        public void bantuanBiasa(String asal, String tujuan, int mid) {
            //Add asal ke priority queue
            pq.add(new Vertex(asal, 0));

            //Jarak dari asal ke asal diganti jadi 0
            dist[adj.get(asal)] = mid;
            while(!pq.isEmpty()) { //O(M^2+E^2+ME)
                
                //remove vertex dengan distance terkecil
                String v = pq.remove().vertex;
                
                //Masukan ke "awan hijau"
                hijau.add(v);
                
                dijkstraBiasa(v, tujuan);
            }
        }
        
        private void dijkstraBiasa(String v, String tujuan) {
            int edgeDistance = -1;
            int newDistance = -1;
            for (int i = 0; i < adjacencyList.get(v).size(); i++) { //O(M+E)
                Edge e = adjacencyList.get(v).get(i); //Get edge/ruas jalan
                if (!hijau.contains(e.B)) {
                    edgeDistance = e.C; //edgeDistance = waktu tempuh jalan
                    if (e.khusus == true) { //Jika jalur khusus, skip
                        continue;
                    } else {
                        newDistance = dist[adj.get(v)] + edgeDistance;
                        if (e.T < newDistance) { //Jika jalan sudah ditutup, skip
                            continue;
                        } else {
                            if (newDistance < dist[adj.get(e.B)]) {
                                dist[adj.get(e.B)] = newDistance;
                            }
                            pq.add(new Vertex(e.B, dist[adj.get(e.B)]));
                        }
                    }
                }
            }
        }
        
        //
        //Method untuk TANYA_EX
        //
        
        public int tanyaEx(String asal, String tujuan, int l, int r) {
            dist = new int[adjacencyList.size()];
            hijau = new HashSet<String>();
            pq = new PriorityQueue<Vertex>(adjacencyList.size(), new Vertex());
            
            for (int i = 0; i < adjacencyList.size(); i++) {
                dist[i] = Integer.MAX_VALUE;
            }
            
            if (r < l) {
                return r;
            }
            
            int mid = (l+r)/2;
            bantuanEx(asal, tujuan, mid);
            
            //Jika Izuri tidak sampai tujuan
            if (dist[adj.get(tujuan)] == Integer.MAX_VALUE) {
                return tanyaEx(asal, tujuan, l, mid-1);
            //Else
            } else {
                return tanyaEx(asal, tujuan, mid + 1, r);
            }
        }
        
        //Method untuk bantuan Dijkstra
        public void bantuanEx(String asal, String tujuan, int mid) {
            //Add asal ke priority queue
            pq.add(new Vertex(asal, 0));

            //Jarak dari asal ke asal diganti jadi 0
            dist[adj.get(asal)] = mid;
            while(!pq.isEmpty()) {
                
                //remove vertex dengan distance terkecil
                String v = pq.remove().vertex;
                
                //Masukan ke "awan hijau"
                hijau.add(v);
                
                dijkstraKhusus(v, tujuan);
            }
        }
        
        //Method untuk dijkstra khusus
        private void dijkstraKhusus(String v, String tujuan) {
            int edgeDistance = -1;
            int newDistance = -1;
            for (int i = 0; i < adjacencyList.get(v).size(); i++) { //O(M+E)
                Edge e = adjacencyList.get(v).get(i);
                if (!hijau.contains(e.B)) {
                    edgeDistance = e.C;
                    if (e.khusus == false) { //Jika jalur biasa, skip
                        continue;
                    } else {
                        newDistance = dist[adj.get(v)] + edgeDistance;
                        if (e.T < newDistance) {
                            continue;
                        } else {
                            if (newDistance < dist[adj.get(e.B)]) {
                                dist[adj.get(e.B)] = newDistance;
                            }
                            pq.add(new Vertex(e.B, dist[adj.get(e.B)]));
                        }
                    }
                }
            }
        }
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