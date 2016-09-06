package com.reprocess.grid_100.interpolation;

/**
 * Created by ZhouXiang on 2016/9/6.
 */
public class NiMatrix {
    public double[][] getNiMatrix(double[][] matrix) {//求逆矩阵函数

        /*定义扩展矩阵*/
        double[][] expand_matrix = new double[matrix.length][matrix.length * 2];
        /*定义得到的逆矩阵*/
        double[][] new_matrix = new double[matrix.length][matrix.length];
        /*初始化扩展矩阵*/
        initExpandMatrix(matrix,expand_matrix);
        /*调整扩展矩阵，若某一列全为0，则行列式的值等于0，不存在逆矩阵*/
        boolean canAdjust = adjustMatrix(expand_matrix);
        if(false == canAdjust)//如果不存在逆矩阵，返回NULL
            return null;
        /*计算扩展矩阵*/
        calculateExpandMatrix(expand_matrix);
        /*用计算过的扩展矩阵取后面的N*N矩阵，为所求*/
        getNewMatrix(expand_matrix,new_matrix);

        return new_matrix;
    }

    /*初始化扩展矩阵*/
    private void initExpandMatrix(double[][] init_matrix,double[][] expand_matrix) {

        for (int i = 0; i < expand_matrix.length; i++)
            for (int j = 0; j < expand_matrix[i].length; j++) {
                if (j < expand_matrix.length) {//左边的N*N矩阵原样赋值
                    expand_matrix[i][j] = init_matrix[i][j];
                } else {    //右边N*N赋值为单位矩阵
                    if (j == expand_matrix.length + i)//如果为右边矩阵的对角线就赋值为1
                        expand_matrix[i][j] = 1;
                    else
                        expand_matrix[i][j] = 0;
                }
            }

    }

    /*调整扩展矩阵，若某一列全为0，则行列式的值等于0，不存在逆矩阵*/
    private boolean adjustMatrix(double[][] expand_matrix) {

        for (int i = 0; i < expand_matrix.length; i++) {
            if (expand_matrix[i][i] == 0) {//如果某行对角线数值为0
                int j;
                /*搜索该列其他不为0的行，如果都为0，则返回false*/
                for (j = 0; j < expand_matrix.length; j++) {

                    if (expand_matrix[j][i] != 0) {//如果有不为0的行，交换这两行
                        double[] temp = expand_matrix[i];
                        expand_matrix[i] = expand_matrix[j];
                        expand_matrix[j] = temp;
                        break;
                    }

                }
                if (j >= expand_matrix.length) {//没有不为0的行
                    System.out.println("此矩阵没有逆矩阵");
                    return false;
                }
            }
        }
        return true;
    }
    /*计算扩展矩阵*/
    private void calculateExpandMatrix(double[][] expand_matrix) {

        for (int i = 0; i < expand_matrix.length; i++) {

            double first_element = expand_matrix[i][i];

            for (int j = 0; j < expand_matrix[i].length; j++)

                expand_matrix[i][j] /= first_element;//将该行所有元素除以首元素

            /*把其他行再该列的数值都化为0*/
            for (int m = 0; m < expand_matrix.length; m++) {
                if (m == i)//遇到自己的行跳过
                    continue;

                double beishu = expand_matrix[m][i];
                for (int n = 0; n < expand_matrix[i].length; n++) {
                    expand_matrix[m][n] -= expand_matrix[i][n] * beishu;
                }
            }

        }

    }
    /*用计算过的扩展矩阵取后面的N*N矩阵，为所求*/
    private void getNewMatrix(double[][] expand_matrix, double[][] new_matrix) {

        for(int i = 0; i < expand_matrix.length; i++)
            for(int j = 0; j < expand_matrix[i].length; j++){
                if(j >= expand_matrix.length)
                    new_matrix[i][j-expand_matrix.length] = expand_matrix[i][j];
            }

    }

    /*打印矩阵*/
    public void printMatrix(double[][] matrix){

        for (double[] tempi : matrix) {
            for (double tempj : tempi) {
                System.out.print(tempj + "  ");
            }
            System.out.println();
        }

    }
    /*矩阵做乘法，验证结果*/
    public static double[][] getProductMatrix(double[][] init_matrix,
                                              double[][] new_matrix) {

        int len = init_matrix.length;
        double[][] product_matrix = new double[len][len];
        for(int i = 0; i < len; i++){
            for(int j = 0; j < len; j++)
                for(int k = 0; k < len; k++)
                    product_matrix[i][j] += init_matrix[i][k] * new_matrix[k][j];
        }
        return product_matrix;
    }


    public static void main(String[] args) {

        NiMatrix _robot = new NiMatrix();

        System.out.println("=====原矩阵=====");
        double init_matrix[][] = {
                { 1, 2, -1 },
                { 3, 4, -2 },
                { 5, -4, 1 }
        };
        _robot.printMatrix(init_matrix);

        System.out.println("=====逆矩阵=====");
        double new_matrix[][] = _robot.getNiMatrix(init_matrix);
        _robot.printMatrix(new_matrix);

        System.out.println("=====原矩阵*逆矩阵=====");
        double[][] product_matrix = getProductMatrix(init_matrix,new_matrix);
        _robot.printMatrix(product_matrix);
    }
}
