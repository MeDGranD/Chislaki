package org.example;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class SZSVResolver {

    public static RealMatrix[] getSZSV(RealMatrix mat, double tol, int maxIter){

        int rows = mat.getRowDimension();
        int cols = mat.getColumnDimension();

        //Матрица для нахождения значений СВ
        RealMatrix ansU = null;
        //Итерация с ограничением
        for(int iter = 0; iter < maxIter; ++iter){

            int[] ij = new int[2];
            double max = 0;
            for(int i = 0; i < rows; ++i){
                for(int j = 0; j < cols; ++j){
                    if(i < j && max < Math.abs(mat.getEntry(i, j))){
                        max = Math.abs(mat.getEntry(i, j));
                        ij[0] = i; ij[1] = j;
                    }
                }
            }

            int i = ij[0];
            int j = ij[1];

            double phi = mat.getEntry(i, i) == mat.getEntry(j, j) ? Math.PI / 4 : Math.atan(2*(mat.getEntry(i, j)/(mat.getEntry(i, i) - mat.getEntry(j, j))))/2;

            //Генерируем матрицу вращения
            RealMatrix U = MatrixUtils.createRealIdentityMatrix(rows);
            U.setEntry(i, i, Math.cos(phi));
            U.setEntry(j, j, Math.cos(phi));
            U.setEntry(i, j, -Math.sin(phi));
            U.setEntry(j, i, Math.sin(phi));

            RealMatrix mA = mat.copy(), mU = U.copy();
            if(ansU == null){
                ansU = mU;
            }
            else{
                ansU = ansU.multiply(mU);
            }
            mat = mU.transpose().multiply(mA).multiply(mU);

            //Вычисления критерия остановки
            double sum = 0;
            for(int r = 0; r < rows; ++r){
                for(int c = 0; c < cols; ++c){
                    if(r < c){
                        sum += 2 * mat.getEntry(r, c) * mat.getEntry(r, c);
                    }
                }
            }
            if(sum < tol){
                break;
            }

        }

        return new RealMatrix[]{mat, ansU};

    }

}
