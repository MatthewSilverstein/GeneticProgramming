package util;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class InputHandler {
	public static Polynomial getPolynomial(){
		while(true){
			Scanner input = new Scanner(System.in);
			String inputString = input.nextLine();
			if (inputString.length()==0){
				continue;
			}
			StringTokenizer st=new StringTokenizer(inputString);
			ArrayList<Double>coefficients=new ArrayList<Double>();
			while(st.hasMoreTokens()){
				String token=st.nextToken();
				Double a=Double.parseDouble(token);
				coefficients.add(a);
			}
			double[]a=new double[coefficients.size()];
			for (int i=0;i<a.length;i++){
				a[i]=coefficients.get(i);
			}
			return new Polynomial(a);
		}
	}
	
	public static PolynomialComplex getCPolynomial(){
		while(true){
			Scanner input = new Scanner(System.in);
			String inputString = input.nextLine();
			if (inputString.length()==0){
				continue;
			}
			StringTokenizer st=new StringTokenizer(inputString);
			ArrayList<Complex>coefficients=new ArrayList<Complex>();
			while(st.hasMoreTokens()){
				String token=st.nextToken();
				StringTokenizer st2 = new StringTokenizer(token,",");
				Double a = Double.parseDouble(st2.nextToken());
				Double b = Double.parseDouble(st2.nextToken());
				Complex c = new Complex(a,b);
				coefficients.add(c);
			}
			Complex[]a=new Complex[coefficients.size()];
			for (int i=0;i<a.length;i++){
				a[i]=coefficients.get(i);
			}
			return new PolynomialComplex(a);
		}
	}
}
