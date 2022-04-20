//package com.shredding;

import java.text.ParseException;
import java.util.Scanner;
import java.util.List;

import java.util.ArrayList;


public class Main {


    public static void main(String[] args) throws Exception {
        BTree a = new BTree(2);
//        FileReader reader = new FileReader("B_11.txt");
//        Scanner file = new Scanner(reader);

        Scanner in = new Scanner(System.in);
        int N = Integer.parseInt(in.nextLine());
//        int N = Integer.parseInt(file.nextLine());

        for (int i = 0; i < N; i++) {
            String[] linesplit = in.nextLine().split(" ");
//            String[] linesplit = file.nextLine().split(" ");
            if(linesplit[0].equals("REPORT")){
                int from = Integer.parseInt(linesplit[2].replace("-",""));
                int to = Integer.parseInt(linesplit[4].replace("-",""));
                var c = a.lookupRange(from,to);
                int res = 0;
                for (int j = 0; j < c.size(); j++) {
                    res += (int) c.get(j);
                }
                System.out.println(res);
            }else {
                int date = Integer.parseInt(linesplit[0].replace("-", ""));
                int unit;


                if (linesplit[1].equals("DEPOSIT")) {
                    unit = Integer.parseInt(linesplit[2]);
                } else {
                    unit = -Integer.parseInt(linesplit[2]);
                }
//                if(date>=26241218 && date<=26250107){
//                    System.out.println(date + "  " + unit);
//                }
                a.add(date, unit);
            }
        }

    }
}

//package com.shredding;


interface RangeMap<K,V> {
    int size();
    boolean isEmpty();
    void add(K key, V value);
    boolean contains(K key);
    V lookup(K key);
    List<V> lookupRange(K from, K to); // lookup values for a range of keys

    // optional: remove an item from a map (+1% extra credit)
//    Object remove(K key);
}

//package com.shredding;


class BTree<K extends Comparable<K> ,V> implements RangeMap<K,V> {
    int max_degree;
    BTreeNode root;
    private int size = 0;
    private int MinDeg;


    public BTree(int deg) {
        this.root = null;
        this.MinDeg = deg;

//        element<K,V> item = new element<K,V>(key,value);

    }




    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size==0;
    }

    @Override
    public void add(K key, V value) {
        element item = new element(key,value);
        size++;

        if (root == null){

            root = new BTreeNode(MinDeg,true);
            root.items[0] = item;
            root.num = 1;
        }else{
            // Когда корневой узел заполнится, дерево станет выше
            if (root.num == 2*MinDeg-1){
                BTreeNode s = new BTreeNode(MinDeg,false);
                // Старый корневой узел становится дочерним узлом нового корневого узла
                s.children[0] = root;
                // Отделяем старый корневой узел и даем ключ новому узлу
                s.splitChild(0,root);
                // Новый корневой узел имеет 2 дочерних узла, переместите туда старый корневой узел
                int i = 0;
                if (key.compareTo((K) s.items[0].key) > 0)
                    i++;
                s.children[i].insertNotFull(item);

                root = s;
            }
            else
                root.insertNotFull(item);
        }

    }

    @Override
    public boolean contains(K key) {
        BTreeNode res = root.search(key);
        if (root == null){
            return false;
        }
        for (int i = 0; i < res.num; i++) {
            if (res.items[i].key.equals(key)){
                return true;
            }
        }
        return false;
    }

    @Override
    public V lookup(K key) {
        BTreeNode res = root.search(key);
        if (root == null){
            return null;
        }
        for (int i = 0; i < res.num; i++) {
            if (res.items[i].key.equals(key)){
                return (V) res.items[i].value;
            }
        }
        return null;
    }

    @Override
    public List<V> lookupRange(K from, K to) {
        ArrayList<V> res = new ArrayList<V>();
        if (root == null){
            return res;
        }
        rangeres(res,from,to,root);
        return res;
    }

    public boolean rangeres(ArrayList<V> res,K from, K to, BTreeNode node){
        if (node == null){
            return false;
        }
        for (int i = 0; i < node.num; i++) {
            var b = node.items[i];

            if(i==node.num-1){
                if(b.key.compareTo(from)>=0 && b.key.compareTo(to)<=0){
                    res.add((V) node.items[i].value);
                }
                if (b.key.compareTo(to)<=0){
                    rangeres(res,from,to,node.children[i+1]);
                }

                if (b.key.compareTo(from)>=0 || b.key.compareTo(to)>=0){
                    rangeres(res,from,to,node.children[i]);
                }
            }else{
                if(b.key.compareTo(from)>=0 && b.key.compareTo(to)<=0){
                    res.add((V) node.items[i].value);
                }
                if (b.key.compareTo(to)<=0 || b.key.compareTo(from)>=0){
                    rangeres(res,from,to,node.children[i]);
                }
            }




//            if(b.key.compareTo(from)>=0 && b.key.compareTo(to)<=0){
//                if(i==node.num-1){
//                res.add((V) node.items[i].value);
//                rangeres(res,from,to,node.children[i]);
//                rangeres(res,from,to,node.children[i+1]);
//
//                }else{
//                    res.add((V) node.items[i].value);
//                    rangeres(res,from,to,node.children[i]);
//
//                }
//            }
//            if (node.num==1){
//                if (b.key.compareTo(to)<=0){
//                    rangeres(res,from,to,node.children[i+1]);
//                }
//                if (b.key.compareTo(from)>=0){
//                    rangeres(res,from,to,node.children[i]);
//                }
//            }
        }


        return true;
    }

//    @Override
//    public Object remove(Object key) {
//        return null;
//    }
}

//package com.shredding;

class element<K extends Comparable<K> ,V> {
    public K key;
    public V value;

    public element(K key,V value){
        this.key = key;
        this.value = value;
    }

}

//package com.shredding;

class BTreeNode {
    public element[] items; // Ключи узла
    int MinDeg; // Минимальная степень узла B-дерева
    BTreeNode[] children; // дочерний узел
    int num; // Количество ключей узла
    boolean isLeaf; // Истина, если это листовой узел

    public BTreeNode(int deg,boolean isLeaf){

        this.MinDeg = deg;
        this.isLeaf = isLeaf;
        this.items = new element[2*this.MinDeg-1]; // Узел имеет не более 2 * MinDeg-1 ключей
        this.children = new BTreeNode[2*this.MinDeg];
        this.num = 0;
    }


    public void splitChild(int i ,BTreeNode y){

        // Сначала создаем узел, содержащий ключи MinDeg-1 y
        BTreeNode z = new BTreeNode(y.MinDeg,y.isLeaf);
        z.num = MinDeg - 1;

        // Передаем все атрибуты y в z
        for (int j = 0; j < MinDeg-1; j++)
            z.items[j] = y.items[j+MinDeg];
        if (!y.isLeaf){
            for (int j = 0; j < MinDeg; j++)
                z.children[j] = y.children[j+MinDeg];
        }
        y.num = MinDeg-1;

        // Вставляем новый дочерний узел в дочерний узел
        for (int j = num; j >= i+1; j--)
            children[j+1] = children[j];
        children[i+1] = z;

        // Перемещаем ключ по y к этому узлу
        for (int j = num-1;j >= i;j--)
            items[j+1] = items[j];
        items[i] = y.items[MinDeg-1];

        num = num + 1;
    }

    public void insertNotFull(element item){

        int i = num -1; // Инициализируем i индексом самого правого значения

        if (isLeaf){ // Когда это листовой узел
            // Находим, куда нужно вставить новый ключ
            while (i >= 0 && items[i].key.compareTo(item.key) > 0){
                items[i+1] = items[i]; // клавиши возвращаются
                i--;
            }
            items[i+1] = item;
            num = num +1;
        }
        else{
            // Находим позицию дочернего узла, который нужно вставить
            while (i >= 0 && items[i].key.compareTo(item.key)> 0)
                i--;
            if (children[i+1].num == 2*MinDeg - 1){ // Когда дочерний узел заполнен
                splitChild(i+1,children[i+1]);
                // После разделения ключ в середине дочернего узла перемещается вверх, а дочерний узел разделяется на два
                if ( item.key.compareTo(items[i+1].key) > 0)
                    i++;
            }
            children[i+1].insertNotFull(item);
        }
    }


    public <K extends Comparable<K>> BTreeNode search(K key) {
        int i = 0;
        while (i < num && key.compareTo((K) items[i].key) > 0)
            i++;

        if (items[i].key.equals(key))
            return this;
        if (isLeaf)
            return null;
        return children[i].search(key);
    }
}

