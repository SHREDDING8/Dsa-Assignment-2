import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws ParseException {
        Scanner in = new Scanner(System.in);
        String[] ND = in.nextLine().split(" ");
        int N = Integer.parseInt(ND[0]);
        int D = Integer.parseInt(ND[1]);

        double[][] lines = new double[N][5];


        for (int i = 0; i < N;i++){
            double[] lineList = new double[5];
            String[] lineList2;

            String line = in.nextLine();
            String[] line_split = line.split(" ");
            Integer without =Integer.parseInt(line_split[0].replace("-",""));
            lineList[0]=(Double.valueOf(without));
            lineList[1]=(Double.parseDouble(line_split[1].replace("$","")));
            lineList2 = line_split[0].split("-");
            lineList[2] = Double.parseDouble(lineList2[0]);
            lineList[3] = Double.parseDouble(lineList2[1]);
            lineList[4] = Double.parseDouble(lineList2[2]);
            lines[i]=(lineList);
        }
        SortDate.radixsort(lines);

        var mon = new Money(lines,N,D);
        mon.count_alerts();
        System.out.println(mon.alerts);


    }
}
class SortDate {


    public static void radixsort(double[][] input) {

        List<double[]>[] buckets = new ArrayList[input.length];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<double[]>();
        }

        // sort
        boolean flag = false;
        int tmp = -1, divisor = 1;
        while (!flag) {
            flag = true;
            // split input between lists
            for (int i = 0; i < input.length;i++) {
                tmp = (int) (input[i][0] / divisor);
                buckets[tmp % input.length].add(input[i]);
                if (flag && tmp > 0) {
                    flag = false;
                }
            }
            // empty lists into input array
            int a = 0;
            for (int b = 0; b < input.length; b++) {
                for (double[] i : buckets[b]) {
                    input[a++] = i;
                }
                buckets[b].clear();
            }
            // move to next digit
            divisor *= input.length;
        }
    }

}


class Money {
    public double[][] lines;
    public double[] mediana;
    public double[] mediana_sort;
    private int N;
    public int D;
    public int Day;
    public double medianaTreshhold;
    public int alerts = 0;
    private int index_lines = 0;
    int the_oldest_day = 0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");



    public Money(double[][] lines, int N, int D) throws ParseException {
        this.lines = lines;
        this.N = N;
        this.D = D;
        this.Day = 0;
        this.mediana = new double[D];
        this.mediana_sort = new double[D];

        double spent_cur_day = 0;

        for (int i = 0; i < D; i++) {
            if(i!=N) {


                String day_i = String.valueOf((int) lines[index_lines][4]) + "." + String.valueOf((int) lines[index_lines][3]) + "." + String.valueOf((int) lines[index_lines][2]);
                String day_i_1 = String.valueOf((int) lines[index_lines + 1][4]) + "." + String.valueOf((int) lines[index_lines + 1][3]) + "." + String.valueOf((int) lines[index_lines + 1][2]);
                long ms_cur_day = dateFormat.parse(day_i).getTime();
                long ms_next_day = dateFormat.parse(day_i_1).getTime();
                long substract_ms = (ms_next_day-ms_cur_day);
                int substract_days = (int) (substract_ms / (24 * 60 * 60 * 1000));

                if (substract_days>0){
                    spent_cur_day+=lines[index_lines][1];
                    mediana[Day] = spent_cur_day;
                    spent_cur_day = 0;
                    Day+=1;
                    index_lines++;

                    for (int j = 0; j < substract_days-1; j++) {
                        if (Day < D){
                            mediana[Day] = 0;
                            Day+=1;
                        }else{
                            mediana[the_oldest_day] = 0;
                            the_oldest_day+=1;
                            if (the_oldest_day == D){
                                the_oldest_day = 0;
                            }
                        }
                    }
                    if (Day >= D){
                        return;
                    }
                }else{
                    i--;
                    spent_cur_day+=lines[index_lines][1];
                    index_lines++;
                }
            }

        }


    }

    public int count_alerts() throws ParseException {

        double spent_cur_day = 0;



        for (int i = index_lines; i < N ; i++) {

            getmedian();
            if(index_lines==N-1){
                spent_cur_day+=lines[index_lines][1];
                if (spent_cur_day >= medianaTreshhold){
                    alerts+=1;
                }
                return 0;
            }

            String day_i = String.valueOf((int) lines[index_lines][4]) + "." + String.valueOf((int) lines[index_lines][3]) + "." + String.valueOf((int) lines[index_lines][2]);
            String day_i_1 = String.valueOf((int) lines[index_lines + 1][4]) + "." + String.valueOf((int) lines[index_lines + 1][3]) + "." + String.valueOf((int) lines[index_lines + 1][2]);
            long ms_cur_day = dateFormat.parse(day_i).getTime();
            long ms_next_day = dateFormat.parse(day_i_1).getTime();
            long substract_ms = (ms_next_day-ms_cur_day);
            int substract_days = (int) (substract_ms / (24 * 60 * 60 * 1000));
            if (substract_days>0){
                spent_cur_day+=lines[index_lines][1];
                mediana[the_oldest_day] = spent_cur_day;

                if (spent_cur_day >= medianaTreshhold){
                    alerts+=1;
                }

                spent_cur_day = 0;
                index_lines++;
                the_oldest_day+=1;
                if (the_oldest_day == D){
                    the_oldest_day = 0;
                }
                for (int j = 0; j < substract_days-1; j++) {
                    mediana[the_oldest_day] = 0;
                    the_oldest_day+=1;
                    if (the_oldest_day == D){
                        the_oldest_day = 0;
                    }
                }

            }else{
                i--;
                spent_cur_day+=lines[index_lines][1];
                if (spent_cur_day >= medianaTreshhold){
                    alerts+=1;
                }
                index_lines++;
            }

        }

        return 0;
    }

    public void getmedian() {
        mediana_sort = mediana.clone();
        sort_mediana_merge(mediana_sort, D);
        if (this.D % 2 == 1) {
            medianaTreshhold = mediana_sort[(D - 1) / 2] * 2;
        } else {
            medianaTreshhold = mediana_sort[(D - 1) / 2] + mediana_sort[((D - 1) / 2) + 1];
        }

    }

    public static void sort_mediana_merge(double[] a, int n) {
        if (n < 2) {
            return;
        }
        int mid = n / 2;
        double[] l = new double[mid];
        double[] r = new double[n - mid];

        for (int i = 0; i < mid; i++) {
            l[i] = a[i];
        }
        for (int i = mid; i < n; i++) {
            r[i - mid] = a[i];
        }
        sort_mediana_merge(l, mid);
        sort_mediana_merge(r, n - mid);

        merge(a, l, r, mid, n - mid);
    }

    public static void merge(
            double[] a, double[] l, double[] r, int left, int right) {

        int i = 0, j = 0, k = 0;
        while (i < left && j < right) {
            if (l[i] <= r[j]) {
                a[k++] = l[i++];
            } else {
                a[k++] = r[j++];
            }
        }
        while (i < left) {
            a[k++] = l[i++];
        }
        while (j < right) {
            a[k++] = r[j++];
        }
    }
}


