package com.mec.scoresys.test;

import java.util.Calendar;

public class Demo {
	public static void main(String[] args) {
		//System.out.println("jkmk" == true);
		System.out.println(dealYear("全部"));
		System.out.println(dealYear("2014-2015学年"));
		System.out.println(dealTerm("全部"));
		System.out.println("123456".hashCode());
	}

	private static String dealYear(String year) {
		String temp = "";
		if(year.equals("全部")) {
			Calendar now = Calendar.getInstance();
			int currYear = now.get(Calendar.YEAR) % 2000;
			int currMonth = now.get(Calendar.MONTH) + 1;
			int startYear = 14;
			
			for(int i = startYear; i < currYear; i++) {
				if(i == currYear-1) {
					if(currMonth >= 2) {
						temp += " OR SUBSTRING(score_id,9,2)='" + i + "')";
					}
				} else {
					temp += i == startYear ? " AND (" : " OR";
					temp += i != startYear ? " SUBSTRING(score_id,9,2)='" + i + "'" : "SUBSTRING(score_id,9,2)='" + i + "'";
				}
			}
		} else {
			temp = " AND SUBSTRING(score_id,9,2)='" + year.substring(2, 4) + "'";
		}
		return temp;
	}
	private static String dealTerm(String term) {
		String temp = "";
		
		if("全部".equals(term)) {
			
			temp = " AND (SUBSTRING(score_id,11,1)='1' OR SUBSTRING(score_id,11,1)='2')";
		} else {
			System.out.println("quanbu " + term);
			String a = term.substring(1, 2);
			temp = " AND SUBSTRING(score_id,11,1)='" + a + "'";
		}
		return temp;
	}
}
